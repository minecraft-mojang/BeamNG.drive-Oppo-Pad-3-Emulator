package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.GameStorageRepository
import com.example.engine.BeamNGPhysicsSimulator
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("BeamPad Drive", appName)
  }

  @Test
  fun `physics simulator initializes vehicle soft-body nodes and beams`() {
    val simulator = BeamNGPhysicsSimulator()
    assertTrue(simulator.nodes.size >= 18)
    assertTrue(simulator.beams.isNotEmpty())
    assertEquals(0f, simulator.vehicleSpeedKph, 0.001f)

    // Step physics with throttle
    simulator.gearMode = "D"
    simulator.throttleInput = 1.0f
    simulator.step(0.016f)

    assertTrue(simulator.vehicleSpeedKph > 0f)
    assertTrue(simulator.engineRpm > 850)
  }

  @Test
  fun `storage repository provisions persistent game installation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = GameStorageRepository(context)
    val installation = repo.installationState.value
    assertNotNull(installation)
    assertTrue(installation!!.hasExecutable)
    assertTrue(installation.detectedVehicles.isNotEmpty())
  }
}
