package com.codeplanks.home360.listing;


import com.codeplanks.home360.config.MongoConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@DataMongoTest( properties = "de.flapdoodle.mongodb.embedded.version=5.0.5")
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(MongoConfig.class)
public class ListingRepositoryTests {
  @Autowired
  private ListingRepository listingRepository;

  private Listing listing;

  ;

  final List<String> details = Arrays.asList("Very good house with electricity", "Wine cellar",
          "Personal garden");
  final List<String> applicationDocs = Arrays.asList("Source of income",
          "National identity card");
  final List<String> apartmentImages = Arrays.asList("https://pixabay.com/photos/chinese-dragon" +
                  "-asian-culture-2949514/",
          "https://pixabay.com/photos/fantasy-dragon-mountain-light-sage-3159493/");

  @BeforeEach
  public void setup() {
    listing = Listing.builder()
            .title("2-bedroom flat at Ilasamaja")
            .description("Lorem ipsum dolor sit amet consectetur adipscing")
            .furnishing("Full POP, fully tiled, borehole water running, kitchen cabinet, " +
                    "wardrobe")
            .position("Besides Kwara State House of Assembly Housing complex")
            .miscellaneous("Another lorem ipsum incoming at")
            .address(new Address("Baker street", "221B", "Ikeja", "Lagos", "Ikeja"))
            .available(true)
            .agentId(1)
            .availableFrom(new Date())
            .cost(new ListingCost(500000, 50000, 50000, 30000))
            .details(details)
            .facilityQuality(FacilityQuality.NORMAL)
            .petsAllowed(PetsAllowed.TO_BE_ARRANGED)
            .apartmentInfo(new ApartmentInfo(3, 2, 2, ApartmentType.APARTMENT))
            .applicationDocs(applicationDocs)
            .apartmentImages(apartmentImages)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

  }

  @Test
  void example(@Autowired final MongoTemplate mongoTemplate) {
//    System.out.println("current: " + mongoTemplate);
    assertThat(mongoTemplate.getDb()).isNotNull();
  }

  // JUnit test for
  @DisplayName("save listing")
  @Test
  @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
  public void givenListingObject_whenSave_thenReturnSavedListing() {
    // When - action or the behaviour we're testing for
    Listing newListing = listingRepository.save(listing);

    // Then - verify the output
    assertThat(newListing).isNotNull();
    assertThat(newListing.getId()).isNotNull();
    assertThat(newListing.getTitle()).isEqualTo("2-bedroom flat at Ilasamaja");

    assertThat(newListing.getTitle()).isNotEqualTo("3-bedroom flat at Ilasamaja");
  }

  @DisplayName("get all listings")
  @Test
  @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
  public void givenListOfListings_whenFindAll_thenReturnListingsList() {
    // Given - precondition or setup
    Listing listing1 = Listing.builder()
            .title("2-bedroom flat at Ilasamaja")
            .description("Lorem ipsum dolor sit amet consectetur adipscing")
            .furnishing("Full POP, fully tiled, borehole water running, kitchen cabinet, " +
                    "wardrobe")
            .position("Besides Kwara State House of Assembly Housing complex")
            .miscellaneous("Another lorem ipsum incoming at")
            .address(new Address("Baker street", "221B", "Ikeja", "Lagos", "Ikeja"))
            .available(true)
            .agentId(1)
            .availableFrom(new Date())
            .cost(new ListingCost(500000, 50000, 50000, 30000))
            .details(details)
            .facilityQuality(FacilityQuality.NORMAL)
            .petsAllowed(PetsAllowed.TO_BE_ARRANGED)
            .apartmentInfo(new ApartmentInfo(3, 2, 2, ApartmentType.APARTMENT))
            .applicationDocs(applicationDocs)
            .apartmentImages(apartmentImages)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    listingRepository.save(listing);
    listingRepository.save(listing1);

    // When - action or the behaviour we're testing for
    List<Listing> listings = listingRepository.findAll();
    System.out.println("length of listings" + listings.size());

    // Then - verify the output
    assertThat(listings).isNotNull();
    assertThat(listings.size()).isGreaterThan(1);
    assertThat(listings.size()).isPositive();

    assertThat(listings).isNotEmpty();
  }

//   JUnit test for find listing by id
  @DisplayName("get listing by id")
  @Test
  public void givenListingObject_whenFindById_thenReturnListingObject() {
    // Given - precondition or setup
    final List<String> details = Arrays.asList("Very good house with electricity", "Wine cellar",
            "Personal garden");
    final List<String> applicationDocs = Arrays.asList("Source of income",
            "National identity card");
    final List<String> apartmentImages = Arrays.asList("https://pixabay.com/photos/chinese-dragon"
    +
                    "-asian-culture-2949514/",
            "https://pixabay.com/photos/fantasy-dragon-mountain-light-sage-3159493/");

    Listing newListing = Listing.builder()
            .title("2-bedroom flat at Ilasamaja")
            .description("Lorem ipsum dolor sit amet consectetur adipscing")
            .furnishing("Full POP, fully tiled, borehole water running, kitchen cabinet, " +
                    "wardrobe")
            .position("Besides Kwara State House of Assembly Housing complex")
            .miscellaneous("Another lorem ipsum incoming at")
            .address(new Address("Baker street", "221B", "Ikeja", "Lagos", "Ikeja"))
            .available(true)
            .agentId(1)
            .availableFrom(new Date())
            .cost(new ListingCost(500000, 50000, 50000, 30000))
            .details(details)
            .facilityQuality(FacilityQuality.NORMAL)
            .petsAllowed(PetsAllowed.TO_BE_ARRANGED)
            .apartmentInfo(new ApartmentInfo(3, 2, 2, ApartmentType.APARTMENT))
            .applicationDocs(applicationDocs)
            .apartmentImages(apartmentImages)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    listingRepository.save(newListing);
    // When - action or the behaviour we're testing for
    Listing listing = listingRepository.findById(newListing.getId()).get();

    // Then - verify the output
    assertThat(listing).isNotNull();
    assertThat(listing.getId()).isNotNull();
    assertThat(listing.getAgentId()).isNotNull();
    assertThat(listing.getTitle()).isEqualTo("2-bedroom flat at Ilasamaja");
  }
}
