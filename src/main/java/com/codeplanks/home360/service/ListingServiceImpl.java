/* (C)2024-2025 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listing.*;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.UnAuthorizedException;
import com.codeplanks.home360.repository.ListingRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.bson.BsonNull;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * @author Wasiu Idowu
 */
@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

  private final ListingRepository listingRepository;
  private final UserServiceImpl userService;
  private final MongoTemplate mongoTemplate;

  Logger logger = LoggerFactory.getLogger(ListingServiceImpl.class);

  public Listing createListing(ListingDTO request) {
    Integer userId = userService.extractUserId();
    AppUser user = userService.getUserByUserId(userId);
    if (!user.isEnabled()) {
      throw new UnAuthorizedException(
          "You are not authorized to create a listing." + " Please confirm your email to proceed.");
    }

    Listing listing =
        Listing.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .furnishing(request.getFurnishing())
            .position(request.getPosition())
            .miscellaneous(request.getMiscellaneous())
            .address(request.getAddress())
            .agentId(userId)
            .availableFrom(request.getAvailableFrom())
            .cost(request.getCost())
            .details(request.getDetails())
            .facilityQuality(request.getFacilityQuality())
            .petsAllowed(request.getPetsAllowed())
            .apartmentInfo(request.getApartmentInfo())
            .applicationDocs(request.getApplicationDocs())
            .apartmentImages(request.getApartmentImages())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .rented(false)
            .rentDate(null)
            .build();
    Listing savedListing = listingRepository.save(listing);

    logger.info("Listing created successfully: {}", savedListing);
    return savedListing;
  }

  @Override
  public List<ListingWithViewCountDTO> allListings() {
    return listingRepository.getAllListingsWithViewCount();
  }

  public Object deleteListing(String listingId) {
    Integer userId = userService.extractUserId();
    Listing listing = findListingById(listingId);
    Integer agentId = listing.getAgentId();
    if (!Objects.equals(agentId, userId)) {
      throw new UnAuthorizedException("You do not have the permission to delete this listing");
    }

    listingRepository.deleteById(listing.getId());

    return null;
  }

  @Override
  public ListingWithAgentInfo getListingById(String listingId) {
    ListingWithViewCountDTO listing = getListingAndViewCountById(listingId);
    int agentId = listing.getAgent_id();
    AppUser listingAgent = userService.getUserByUserId(agentId);
    ListingAgentInfo agentInfo = ListingMapper.mapToListingAgentInfo(listingAgent);

    return ListingWithAgentInfo.builder().agentInfo(agentInfo).listing(listing).build();
  }

  private ListingWithViewCountDTO getListingAndViewCountById(String listingId) {
    return listingRepository.findListingWithViewCountById(listingId);
  }

  @Override
  public Listing findListingById(String listingId) {
    return listingRepository
        .findById(listingId)
        .orElseThrow(() -> new NotFoundException("Listing not found"));
  }

  @Override
  public PaginatedResponse<Listing> getListingsByAgentId(int page, int size) {
    Integer userId = userService.extractUserId();
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");
    Page<Listing> agentListings = listingRepository.findListingsByAgentId(userId, pageable);
    return PaginatedResponse.<Listing>builder()
        .currentPage(agentListings.getNumber() + 1)
        .totalItems(agentListings.getTotalElements())
        .totalPages(agentListings.getTotalPages())
        .items(agentListings.getContent())
        .hasNext(agentListings.hasNext())
        .build();
  }

  @Override
  public PaginatedResponse<Listing> getFilteredListings(
      int page, int size, String city, int annualRent, String apartmentType) {
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");
    Page<Listing> filteredListings =
        listingRepository.findAllWithFilter(city, annualRent, apartmentType, pageable);
    return PaginatedResponse.<Listing>builder()
        .currentPage(filteredListings.getNumber() + 1)
        .totalItems(filteredListings.getTotalElements())
        .totalPages(filteredListings.getTotalPages())
        .items(filteredListings.getContent())
        .hasNext(filteredListings.hasNext())
        .build();
  }

  @Override
  public Listing updateRentedListing(RentUpdate rentUpdate) {
    Integer userId = userService.extractUserId();
    Listing listingToUpdate =
        listingRepository
            .findById(rentUpdate.listingId)
            .orElseThrow(() -> new NotFoundException("Listing not found"));
    if (!Objects.equals(listingToUpdate.getAgentId(), userId)) {
      throw new AccessDeniedException("You are not authorized to update this listing.");
    }
    listingToUpdate.setRented(rentUpdate.isRented);
    if (rentUpdate.isRented) {
      listingToUpdate.setRentDate(LocalDateTime.now());
    } else {
      listingToUpdate.setRentDate(null);
    }

    return listingRepository.save(listingToUpdate);
  }

  public Document getAggregateListingStats() {
    Integer userId = userService.extractUserId();
    List<Document> pipeline =
        Arrays.asList(
            new Document("$match", new Document("agent_id", userId)),
            new Document(
                "$addFields", new Document("listingIdStr", new Document("$toString", "$_id"))),
            new Document(
                "$facet",
                new Document("totalListings", Arrays.asList(new Document("$count", "total_count")))
                    .append(
                        "rentedListings",
                        Arrays.asList(
                            new Document("$match", new Document("rented", true)),
                            new Document("$count", "total_rented")))
                    .append(
                        "totalIncome",
                        Arrays.asList(
                            new Document("$match", new Document("rented", true)),
                            new Document(
                                "$group",
                                new Document("_id", new BsonNull())
                                    .append(
                                        "total_income",
                                        new Document(
                                            "$sum",
                                            new Document(
                                                "$add",
                                                Arrays.asList(
                                                    "$cost.annual_rent",
                                                    "$cost.agent_fee",
                                                    "$cost.caution_fee",
                                                    "$cost.agreement_fee")))))))
                    .append(
                        "income",
                        Arrays.asList(
                            new Document("$match", new Document("rented", true)),
                            new Document(
                                "$addFields",
                                new Document(
                                    "total_income",
                                    new Document(
                                        "$add",
                                        Arrays.asList(
                                            "$cost.annual_rent",
                                            "$cost.agent_fee",
                                            "$cost.caution_fee",
                                            "$cost.agreement_fee")))),
                            new Document(
                                "$group",
                                new Document(
                                        "_id",
                                        new Document("year", new Document("$year", "$rentDate"))
                                            .append("month", new Document("$month", "$rentDate")))
                                    .append("total_income", new Document("$sum", "$total_income"))),
                            new Document(
                                "$group",
                                new Document("_id", "$_id.year")
                                    .append(
                                        "months",
                                        new Document(
                                            "$push",
                                            new Document(
                                                    "k",
                                                    new Document(
                                                        "$arrayElemAt",
                                                        Arrays.asList(
                                                            Arrays.asList(
                                                                "January",
                                                                "February",
                                                                "March",
                                                                "April",
                                                                "May",
                                                                "June",
                                                                "July",
                                                                "August",
                                                                "September",
                                                                "October",
                                                                "November",
                                                                "December"),
                                                            new Document(
                                                                "$subtract",
                                                                Arrays.asList("$_id.month", 1L)))))
                                                .append("v", "$total_income")))),
                            new Document(
                                "$addFields",
                                new Document(
                                    "months",
                                    new Document(
                                        "$mergeObjects",
                                        Arrays.asList(
                                            new Document(
                                                "$arrayToObject",
                                                new Document(
                                                    "$map",
                                                    new Document(
                                                            "input",
                                                            new Document(
                                                                "$range", Arrays.asList(0L, 12L)))
                                                        .append("as", "i")
                                                        .append(
                                                            "in",
                                                            new Document(
                                                                    "k",
                                                                    new Document(
                                                                        "$arrayElemAt",
                                                                        Arrays.asList(
                                                                            Arrays.asList(
                                                                                "January",
                                                                                "February",
                                                                                "March",
                                                                                "April",
                                                                                "May",
                                                                                "June",
                                                                                "July",
                                                                                "August",
                                                                                "September",
                                                                                "October",
                                                                                "November",
                                                                                "December"),
                                                                            "$$i")))
                                                                .append("v", 0L)))),
                                            new Document("$arrayToObject", "$months"))))),
                            new Document(
                                "$addFields",
                                new Document(
                                    "months",
                                    new Document(
                                        "$map",
                                        new Document(
                                                "input", new Document("$objectToArray", "$months"))
                                            .append("as", "m")
                                            .append(
                                                "in",
                                                new Document("name", "$$m.k")
                                                    .append("amount", "$$m.v"))))),
                            new Document(
                                "$project",
                                new Document("_id", 0L)
                                    .append("year", "$_id")
                                    .append("months", 1L)),
                            new Document("$sort", new Document("year", 1L))))
                    .append(
                        "listings",
                        Arrays.asList(
                            new Document(
                                "$group",
                                new Document(
                                        "_id",
                                        new Document("year", new Document("$year", "$created_at"))
                                            .append("month", new Document("$month", "$created_at")))
                                    .append("count", new Document("$sum", 1L))),
                            new Document(
                                "$group",
                                new Document("_id", "$_id.year")
                                    .append(
                                        "months",
                                        new Document(
                                            "$push",
                                            new Document(
                                                    "k",
                                                    new Document(
                                                        "$arrayElemAt",
                                                        Arrays.asList(
                                                            Arrays.asList(
                                                                "January",
                                                                "February",
                                                                "March",
                                                                "April",
                                                                "May",
                                                                "June",
                                                                "July",
                                                                "August",
                                                                "September",
                                                                "October",
                                                                "November",
                                                                "December"),
                                                            new Document(
                                                                "$subtract",
                                                                Arrays.asList("$_id.month", 1L)))))
                                                .append("v", "$count")))),
                            new Document(
                                "$addFields",
                                new Document(
                                    "months",
                                    new Document(
                                        "$mergeObjects",
                                        Arrays.asList(
                                            new Document(
                                                "$arrayToObject",
                                                new Document(
                                                    "$map",
                                                    new Document(
                                                            "input",
                                                            new Document(
                                                                "$range", Arrays.asList(0L, 12L)))
                                                        .append("as", "i")
                                                        .append(
                                                            "in",
                                                            new Document(
                                                                    "k",
                                                                    new Document(
                                                                        "$arrayElemAt",
                                                                        Arrays.asList(
                                                                            Arrays.asList(
                                                                                "January",
                                                                                "February",
                                                                                "March",
                                                                                "April",
                                                                                "May",
                                                                                "June",
                                                                                "July",
                                                                                "August",
                                                                                "September",
                                                                                "October",
                                                                                "November",
                                                                                "December"),
                                                                            "$$i")))
                                                                .append("v", 0L)))),
                                            new Document("$arrayToObject", "$months"))))),
                            new Document(
                                "$addFields",
                                new Document(
                                    "months",
                                    new Document(
                                        "$map",
                                        new Document(
                                                "input", new Document("$objectToArray", "$months"))
                                            .append("as", "m")
                                            .append(
                                                "in",
                                                new Document("name", "$$m.k")
                                                    .append("amount", "$$m.v"))))),
                            new Document(
                                "$project",
                                new Document("_id", 0L)
                                    .append("year", "$_id")
                                    .append("months", 1L)),
                            new Document("$sort", new Document("year", 1L))))
                    .append(
                        "totalViews",
                        Arrays.asList(
                            new Document(
                                "$lookup",
                                new Document("from", "listingViews")
                                    .append("localField", "listingIdStr")
                                    .append("foreignField", "listingId")
                                    .append("as", "views")),
                            new Document(
                                "$addFields",
                                new Document("viewCount", new Document("$size", "$views"))),
                            new Document(
                                "$group",
                                new Document("_id", new BsonNull())
                                    .append("total_views", new Document("$sum", "$viewCount")))))
                    .append(
                        "viewsByYearAndMonth",
                        Arrays.asList(
                            new Document(
                                "$lookup",
                                new Document("from", "listingViews")
                                    .append("localField", "listingIdStr")
                                    .append("foreignField", "listingId")
                                    .append("as", "views")),
                            new Document("$unwind", "$views"),
                            new Document(
                                "$group",
                                new Document(
                                        "_id",
                                        new Document(
                                                "year", new Document("$year", "$views.timestamp"))
                                            .append(
                                                "month",
                                                new Document("$month", "$views.timestamp")))
                                    .append("total_views", new Document("$sum", 1L))),
                            new Document(
                                "$group",
                                new Document("_id", "$_id.year")
                                    .append(
                                        "months",
                                        new Document(
                                            "$push",
                                            new Document(
                                                    "k",
                                                    new Document(
                                                        "$arrayElemAt",
                                                        Arrays.asList(
                                                            Arrays.asList(
                                                                "January",
                                                                "February",
                                                                "March",
                                                                "April",
                                                                "May",
                                                                "June",
                                                                "July",
                                                                "August",
                                                                "September",
                                                                "October",
                                                                "November",
                                                                "December"),
                                                            new Document(
                                                                "$subtract",
                                                                Arrays.asList("$_id.month", 1L)))))
                                                .append("v", "$total_views")))),
                            new Document(
                                "$addFields",
                                new Document(
                                    "months",
                                    new Document(
                                        "$mergeObjects",
                                        Arrays.asList(
                                            new Document(
                                                "$arrayToObject",
                                                new Document(
                                                    "$map",
                                                    new Document(
                                                            "input",
                                                            new Document(
                                                                "$range", Arrays.asList(0L, 12L)))
                                                        .append("as", "i")
                                                        .append(
                                                            "in",
                                                            new Document(
                                                                    "k",
                                                                    new Document(
                                                                        "$arrayElemAt",
                                                                        Arrays.asList(
                                                                            Arrays.asList(
                                                                                "January",
                                                                                "February",
                                                                                "March",
                                                                                "April",
                                                                                "May",
                                                                                "June",
                                                                                "July",
                                                                                "August",
                                                                                "September",
                                                                                "October",
                                                                                "November",
                                                                                "December"),
                                                                            "$$i")))
                                                                .append("v", 0L)))),
                                            new Document("$arrayToObject", "$months"))))),
                            new Document(
                                "$addFields",
                                new Document(
                                    "months",
                                    new Document(
                                        "$map",
                                        new Document(
                                                "input", new Document("$objectToArray", "$months"))
                                            .append("as", "m")
                                            .append(
                                                "in",
                                                new Document("name", "$$m.k")
                                                    .append("views", "$$m.v"))))),
                            new Document(
                                "$project",
                                new Document("_id", 0L)
                                    .append("year", "$_id")
                                    .append("months", 1L))))
                    .append(
                        "topListingsByViews",
                        Arrays.asList(
                            new Document(
                                "$lookup",
                                new Document("from", "listingViews")
                                    .append("localField", "listingIdStr")
                                    .append("foreignField", "listingId")
                                    .append("as", "views")),
                            new Document(
                                "$addFields",
                                new Document("viewCount", new Document("$size", "$views"))),
                            new Document("$sort", new Document("viewCount", -1L)),
                            new Document("$limit", 5L),
                            new Document("$unset", Arrays.asList("views", "_class"))))),
            new Document(
                "$project",
                new Document(
                        "total_listings",
                        new Document(
                            "$arrayElemAt", Arrays.asList("$totalListings.total_count", 0L)))
                    .append(
                        "rented_listings",
                        new Document(
                            "$arrayElemAt", Arrays.asList("$rentedListings.total_rented", 0L)))
                    .append(
                        "total_income",
                        new Document(
                            "$arrayElemAt", Arrays.asList("$totalIncome.total_income", 0L)))
                    .append("income", "$income")
                    .append("listings", "$listings")
                    .append(
                        "total_views",
                        new Document("$arrayElemAt", Arrays.asList("$totalViews.total_views", 0L)))
                    .append("views_by_year_and_month", "$viewsByYearAndMonth")
                    .append("mostViewedListings", "$topListingsByViews")));

    return mongoTemplate.getDb().getCollection("listings").aggregate(pipeline).first();
  }
}
