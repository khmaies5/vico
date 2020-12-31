package pl.patrykgoworowski.liftchart_core

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pl.patrykgoworowski.liftchart_core.data_set.ArrayListEntryManager
import pl.patrykgoworowski.liftchart_core.data_set.entry.EntryManager.Companion.NO_VALUE
import pl.patrykgoworowski.liftchart_core.entry.entriesOf
import pl.patrykgoworowski.liftchart_core.entry.entryOf

class EntryManagerTests {

    private val entry1 = entryOf(1f, 1f)
    private val entry2 = entryOf(0, 0)
    private val entries3 = entriesOf(3 to 4, 8 to 28)

    @Before
    fun setUp() {

    }

    @Test
    fun testCollectionModifications() {
        val entryCollection = ArrayListEntryManager()

        fun assertSize(size: Int) {
            assertEquals(size, entryCollection.size)
        }

        assertSize(0)

        entryCollection += entry1
        entryCollection += entry2
        assertSize(2)

        entryCollection += entries3
        assertSize(4)

        entryCollection -= entry1
        entryCollection -= entry2
        assertSize(2)

        entryCollection -= entries3
        assertSize(0)
    }

    @Test
    fun testSizes() {
        val entryCollection = ArrayListEntryManager()

        assertEquals(entryCollection.minX, NO_VALUE)
        assertEquals(entryCollection.maxX, NO_VALUE)
        assertEquals(entryCollection.minY, NO_VALUE)
        assertEquals(entryCollection.maxY, NO_VALUE)

        entryCollection += entry1

        assertEquals(entryCollection.minX, 1f)
        assertEquals(entryCollection.maxX, 1f)
        assertEquals(entryCollection.minY, 1f)
        assertEquals(entryCollection.maxY, 1f)

        entryCollection += entry2
        entryCollection += entries3

        assertEquals(entryCollection.minX, 0f)
        assertEquals(entryCollection.maxX, 8f)
        assertEquals(entryCollection.minY, 0f)
        assertEquals(entryCollection.maxY, 28f)

    }


}