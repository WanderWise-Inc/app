package com.github.wanderwise_inc.app.viewmodel

/*
class ProfileViewModelTest {
  private lateinit var profileRepository: ProfileRepository
  private lateinit var imageRepository: ImageRepository
  private lateinit var profileViewModel: ProfileViewModel

  @Mock private lateinit var mockApplication: Application

  val ethanProfile =
      Profile(
          uid = "",
          displayName = "Ethan g",
          userUid = "ethan",
          bio = "",
      )
  val engjellProfile =
      Profile(
          uid = "",
          displayName = "engjell uwu",
          userUid = "engjell",
          bio = "",
      )

  @Before
  fun setup() {
    profileRepository = ProfileRepositoryTestImpl()
    mockApplication = ApplicationProvider.getApplicationContext<Application>()
    imageRepository = ImageRepositoryTestImpl(mockApplication)
    profileViewModel = ProfileViewModel(profileRepository, imageRepository)
  }

  @Test
  fun adding_profiles_test() = runTest {
    // set profiles
    profileViewModel.setProfile(ethanProfile)
    profileViewModel.setProfile(engjellProfile)

    val query = profileViewModel.getAllProfiles().first()

    assert(query.contains(ethanProfile))
    assert(query.contains(engjellProfile))

    for (profile in query) {
      // UID's must not be blank after setting in DB
      assert(profile.uid.isNotBlank())
    }
  }

  @Test
  fun adding_profile_twice_test() = runTest {
    profileViewModel.setProfile(ethanProfile)
    profileViewModel.setProfile(ethanProfile)

    val query = profileViewModel.getAllProfiles().first()

    assert(query.size == 1)
    assert(query == listOf(ethanProfile))
  }

  @Test
  fun getting_non_existing_profile_returns_null() = runTest {
    val queryRes = profileViewModel.getProfile("doesn't exist").first()
    assert(queryRes == null)
  }

  @Test
  fun delete_profile_test() = runTest {
    profileViewModel.setProfile(ethanProfile)
    profileViewModel.setProfile(engjellProfile)
    assert(ethanProfile.uid.isNotBlank())

    profileViewModel.deleteProfile(ethanProfile)
    val allProfiles = profileViewModel.getAllProfiles().first()

    assert(!allProfiles.contains(ethanProfile))
    assert(allProfiles.contains(engjellProfile))
    assert(allProfiles.size == 1)
  }
}
*/
