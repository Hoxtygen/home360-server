/* (C)2024-2025 */
package com.codeplanks.home360.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.UnsetOperation.unset;

import com.codeplanks.home360.domain.listing.Listing;
import com.codeplanks.home360.domain.listing.ListingWithViewCountDTO;
import com.codeplanks.home360.exception.NotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class CustomListingRepositoryImpl implements CustomListingRepository {
  @Autowired MongoTemplate mongoTemplate;

  @Override
  public Page<Listing> findAllWithFilter(
      String city, int annualRent, String apartmentType, Pageable pageable) {
    // Todo: Only listings not rented out should be shown
    Query query =
        constructFilterQuery(city, annualRent, apartmentType)
            .with(pageable)
            .collation(getEnglishCollation());
    List<Listing> listings = mongoTemplate.find(query, Listing.class, "listings");
    return PageableExecutionUtils.getPage(
        listings, pageable, () -> mongoTemplate.count(query.limit(-1).skip(-1), Listing.class));
  }

  @Override
  public Page<Listing> findListingsByAgentId(Integer agentId, Pageable pageable) {
    Query query = fetchAgentListings(agentId).with(pageable).collation(getEnglishCollation());
    List<Listing> listings = mongoTemplate.find(query, Listing.class, "listings");
    return PageableExecutionUtils.getPage(
        listings, pageable, () -> mongoTemplate.count(query.limit(-1).skip(-1), Listing.class));
  }

  @Override
  public ListingWithViewCountDTO findListingWithViewCountById(String listingId) {
    Aggregation aggregation =
        newAggregation(
            match(Criteria.where("_id").is(new ObjectId(listingId))),
            addFields()
                .addFieldWithValue("listingIdStr", ConvertOperators.ToString.toString("$_id"))
                .build(),
            lookup("listingViews", "listingIdStr", "listingId", "views"),
            addFields()
                .addFieldWithValue("viewCount", ArrayOperators.Size.lengthOfArray("views"))
                .build(),
            unset("views", "listingIdStr"));

    AggregationResults<ListingWithViewCountDTO> results =
        mongoTemplate.aggregate(aggregation, "listings", ListingWithViewCountDTO.class);
    return results.getMappedResults().stream()
        .findFirst()
        .orElseThrow(() -> new NotFoundException("Listing not found"));
  }

  @Override
  public List<ListingWithViewCountDTO> getAllListingsWithViewCount() {
    Aggregation aggregation =
        Aggregation.newAggregation(
            addFields()
                .addFieldWithValue("listingIdStr", ConvertOperators.ToString.toString("$_id"))
                .build(),
            lookup("listingViews", "listingIdStr", "listingId", "views"),
            addFields()
                .addFieldWithValue("viewCount", ArrayOperators.Size.lengthOfArray("views"))
                .build(),
            unset("views"));

    AggregationResults<ListingWithViewCountDTO> results =
        mongoTemplate.aggregate(aggregation, "listings", ListingWithViewCountDTO.class);

    return results.getMappedResults();
  }

  private Query fetchAgentListings(Integer agentId) {
    return new Query(Criteria.where("agentId").is(agentId));
  }

  private Collation getEnglishCollation() {
    return Collation.of("en").strength(2);
  }

  private Query constructFilterQuery(String city, int annualRent, String apartmentType) {
    Query query = new Query();

    if (city != null && !city.isEmpty()) {
      query.addCriteria(Criteria.where("address.city").is(city));
    }

    if (apartmentType != null && !apartmentType.isEmpty()) {
      query.addCriteria(
          Criteria.where("apartment_info.apartment_type").is(apartmentType.toUpperCase()));
    }

    query.addCriteria(Criteria.where("cost.annual_rent").gte(annualRent));
    query.addCriteria(Criteria.where("rented").is(false));

    return query;
  }

  private MatchOperation matchAgentId(String agentId) {
    return Aggregation.match(Criteria.where("agent_id").is(agentId));
  }

  private LookupOperation lookupListingViews() {
    return Aggregation.lookup("listingViews", "_id", "listingId", "views");
  }

  private AddFieldsOperation addViewCount() {
    return Aggregation.addFields()
        .addFieldWithValue("viewCount", ArrayOperators.Size.lengthOfArray("views"))
        .build();
  }

  private GroupOperation groupTotalViews() {
    return Aggregation.group().sum("viewCount").as("total_views");
  }

  private UnwindOperation unwindViews() {
    return Aggregation.unwind("views");
  }

  //  private GroupOperation groupViewsByYearAndMonth() {
  //    return Aggregation.group()
  //            .and("year($views.timestamp)").as("year")
  //            .and("month($views.timestamp)").as("month")
  //            .sum(1).as("total_views");
  //  }

}
