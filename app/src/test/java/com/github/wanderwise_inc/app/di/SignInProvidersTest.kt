package com.github.wanderwise_inc.app.di

import com.firebase.ui.auth.AuthUI
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SignInProvidersTest {

    private lateinit var googleConfig: AuthUI.IdpConfig

    @Before
    fun setup() {
        // Mock the AuthUI.IdpConfig
        googleConfig = mockk(relaxed = true)

        // Mock the SignInProviders object
        mockkObject(SignInProviders)

        // When google is accessed, return the mocked AuthUI.IdpConfig
        every { SignInProviders.google } returns googleConfig
    }

    @After
    fun teardown() {
        // Unmock the SignInProviders object after each test
        unmockkObject(SignInProviders)
    }

    @Test
    fun testProviders() {
        // Test the providers property
        val providers = SignInProviders.providers
        assertEquals(1, providers.size)
        assertEquals(googleConfig, providers[0])
    }
}