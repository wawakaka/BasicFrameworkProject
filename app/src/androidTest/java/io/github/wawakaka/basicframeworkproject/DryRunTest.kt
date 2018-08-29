package io.github.wawakaka.basicframeworkproject

import org.junit.After
import org.junit.Test
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.test.KoinTest
import org.koin.test.dryRun

/**
 * Created by wawakaka on 06/08/18.
 */
class DryRunTest : KoinTest {

    @After
    fun after() {
        closeKoin()
    }

    @Test
    fun dryRunTest() {
        dryRun()
    }
}