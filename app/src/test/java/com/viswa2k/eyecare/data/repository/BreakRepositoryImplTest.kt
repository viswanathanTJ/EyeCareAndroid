package com.viswa2k.eyecare.data.repository

import com.viswa2k.eyecare.data.db.dao.BreakEventDao
import com.viswa2k.eyecare.data.db.dao.DailyStatsDao
import com.viswa2k.eyecare.data.db.entity.BreakEvent
import com.viswa2k.eyecare.data.db.entity.BreakEventType
import com.viswa2k.eyecare.data.db.entity.DailyStats
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BreakRepositoryImplTest {

    private lateinit var breakEventDao: BreakEventDao
    private lateinit var dailyStatsDao: DailyStatsDao
    private lateinit var repository: BreakRepositoryImpl

    @Before
    fun setup() {
        breakEventDao = mockk(relaxed = true)
        dailyStatsDao = mockk(relaxed = true)
        repository = BreakRepositoryImpl(breakEventDao, dailyStatsDao)
    }

    @Test
    fun `recordBreakEvent TAKEN increments breaksTaken and totalCycles`() = runTest {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        coEvery { dailyStatsDao.getByDate(today) } returns null
        coEvery { dailyStatsDao.getStreakForDate(any()) } returns 0

        repository.recordBreakEvent(BreakEventType.TAKEN)

        val statsSlot = slot<DailyStats>()
        coVerify { dailyStatsDao.upsert(capture(statsSlot)) }
        assertEquals(1, statsSlot.captured.breaksTaken)
        assertEquals(1, statsSlot.captured.totalCycles)
    }

    @Test
    fun `recordBreakEvent SKIPPED increments breaksSkipped and totalCycles`() = runTest {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        coEvery { dailyStatsDao.getByDate(today) } returns null
        coEvery { dailyStatsDao.getStreakForDate(any()) } returns 0

        repository.recordBreakEvent(BreakEventType.SKIPPED)

        val statsSlot = slot<DailyStats>()
        coVerify { dailyStatsDao.upsert(capture(statsSlot)) }
        assertEquals(1, statsSlot.captured.breaksSkipped)
        assertEquals(1, statsSlot.captured.totalCycles)
        assertEquals(0, statsSlot.captured.breaksTaken)
    }

    @Test
    fun `recordBreakEvent inserts break event`() = runTest {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        coEvery { dailyStatsDao.getByDate(today) } returns null
        coEvery { dailyStatsDao.getStreakForDate(any()) } returns 0

        repository.recordBreakEvent(BreakEventType.TAKEN)

        val eventSlot = slot<BreakEvent>()
        coVerify { breakEventDao.insert(capture(eventSlot)) }
        assertEquals(BreakEventType.TAKEN, eventSlot.captured.type)
    }

    @Test
    fun `recordBreakEvent accumulates on existing stats`() = runTest {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val existing = DailyStats(date = today, breaksTaken = 5, totalCycles = 8)
        coEvery { dailyStatsDao.getByDate(today) } returns existing
        coEvery { dailyStatsDao.getStreakForDate(any()) } returns 2

        repository.recordBreakEvent(BreakEventType.TAKEN)

        val statsSlot = slot<DailyStats>()
        coVerify { dailyStatsDao.upsert(capture(statsSlot)) }
        assertEquals(6, statsSlot.captured.breaksTaken)
        assertEquals(9, statsSlot.captured.totalCycles)
    }

    @Test
    fun `streak increments from yesterday when breaks taken`() = runTest {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        coEvery { dailyStatsDao.getByDate(today) } returns null
        coEvery { dailyStatsDao.getStreakForDate(any()) } returns 5

        repository.recordBreakEvent(BreakEventType.TAKEN)

        val statsSlot = slot<DailyStats>()
        coVerify { dailyStatsDao.upsert(capture(statsSlot)) }
        assertEquals(6, statsSlot.captured.streakDays)
    }
}
