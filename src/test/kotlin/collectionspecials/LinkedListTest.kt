package tool.collectionspecials

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class LinkedListTest {

    @Test
    fun checkSize() {
        var ll = emptyLinkedList<Int>()
        assertEquals(0, ll.size)

        ll = (1..1).toLinkedList()
        assertEquals(1, ll.size)

        ll = (1..4).toLinkedList()
        assertEquals(4, ll.size)
    }

    @Test
    fun firstIndexOrNull() {
        var ll = emptyLinkedList<Int>()
        assertNull(ll.firstIndexOrNull())

        ll = (1..1).toLinkedList()
        assertNotNull(ll.firstIndexOrNull())
        assertEquals(1, ll[ll.firstIndexOrNull()!!])

        ll = (1..4).toLinkedList()
        assertNotNull(ll.firstIndexOrNull())
        assertEquals(1, ll[ll.firstIndexOrNull()!!])
    }

    @Test
    fun lastIndexOrNull() {
        var ll = emptyLinkedList<Int>()
        assertNull(ll.lastIndexOrNull())

        ll = (1..1).toLinkedList()
        assertNotNull(ll.lastIndexOrNull())
        assertEquals(1, ll[ll.lastIndexOrNull()!!])

        ll = (1..4).toLinkedList()
        assertNotNull(ll.lastIndexOrNull())
        assertEquals(4, ll[ll.lastIndexOrNull()!!])
    }

    @Test
    fun firstIndex() {
        var ll = emptyLinkedList<Int>()
        assertThrows<LinkedListException> { ll.firstIndex() }

        ll = (1..1).toLinkedList()
        assertEquals(1, ll[ll.firstIndex()])

        ll = (1..4).toLinkedList()
        assertEquals(1, ll[ll.firstIndex()])
    }

    @Test
    fun lastIndex() {
        var ll = emptyLinkedList<Int>()
        assertThrows<LinkedListException> { ll.lastIndex() }

        ll = (1..1).toLinkedList()
        assertEquals(1, ll[ll.lastIndex()])

        ll = (1..4).toLinkedList()
        assertEquals(4, ll[ll.lastIndex()])
    }

    @Test
    fun isEmpty() {
        var ll = emptyLinkedList<Int>()
        assertTrue(ll.isEmpty())

        ll = (1..1).toLinkedList()
        assertFalse(ll.isEmpty())

        ll = (1..4).toLinkedList()
        assertFalse(ll.isEmpty())
    }

    @Test
    fun get() {
        val ll = (1..4).toLinkedList()
        var p = ll.firstIndex()
        assertEquals(1, ll[p])
        p = p.next()
        assertEquals(2, ll[p])
        p = p.next()
        assertEquals(3, ll[p])
        p = p.next()
        assertEquals(4, ll[p])
        p = p.next()
        assertThrows<LinkedListException> { ll[p] }
    }

    @Test
    fun set() {
        val ll = (1..4).toLinkedList()
        var p = ll.firstIndex()
        ll[p] = 100
        p = p.next()
        ll[p] = 200
        p = ll.lastIndex()
        ll[p] = 400
        val list = ll.toList()
        assertEquals(100, list[0])
        assertEquals(200, list[1])
        assertEquals(3, list[2])
        assertEquals(400, list[3])
    }

    @Test
    fun contains() {
        var ll = (1..4).toLinkedList()
        assertTrue(ll.contains(1))
        assertTrue(ll.contains(3))
        assertTrue(ll.contains(4))
        assertFalse(ll.contains(11))
        ll = emptyLinkedList()
        assertFalse(ll.contains(2))
    }

    @Test
    fun add() {
        val ll = emptyLinkedList<Int>()
        ll.add(1)
        assertEquals(1, ll.first())
        assertEquals(1, ll.last())
        val p1 = ll.firstIndex()

        ll.add(2)
        assertEquals(1, ll.first())
        assertEquals(2, ll.last())
        val p2 = ll.lastIndex()

        ll.add(3)
        assertEquals(1, ll.first())
        assertEquals(3, ll.last())
        val p3 = ll.lastIndex()

        ll.add(p1, 100)
        assertEquals(100, ll.first())
        assertEquals(1, ll[p1])

        ll.add(p2, 200)
        ll.add(p3, 300)

        val list = ll.toList()
        assertEquals(listOf(100, 1,200,2,300,3), list)
    }

    @Test
    fun removeAt() {
        var ll = (1..1).toLinkedList()
        ll.removeAt(ll.firstIndex())
        assertTrue(ll.isEmpty())

        ll = (1..4).toLinkedList()
        ll.removeAt(ll.firstIndex())
        ll.removeAt(ll.lastIndex())
        assertEquals(listOf(2,3), ll.toList())

        ll = (1..4).toLinkedList()
        ll.removeAt(ll.firstIndex().next())

        ll.removeAt(ll.lastIndex().prev())
        assertEquals(listOf(1,4), ll.toList())
    }

    @Test
    fun remove() {
        var ll = (1..1).toLinkedList()
        ll.remove(1)
        assertTrue(ll.isEmpty())

        ll = (1..4).toLinkedList()
        ll.remove(1)
        ll.remove(4)
        assertEquals(listOf(2,3), ll.toList())

        ll = (1..4).toLinkedList()
        ll.remove(2)
        ll.remove(3)
        assertEquals(listOf(1,4), ll.toList())

        ll = listOf(1, 1, 2, 2, 3, 4).toLinkedList()
        assertTrue(ll.remove(1))
        assertEquals(listOf(1, 2, 2, 3, 4), ll.toList())

        ll = listOf(1, 1, 2, 2, 3, 4).toLinkedList()
        assertTrue(ll.remove(2))
        assertEquals(listOf(1, 1, 2, 3, 4), ll.toList())
    }

    @Test
    fun firstIndexOfOrNull() {
        var ll = (1..1).toLinkedList()
        assertEquals(ll.firstIndex(), ll.firstIndexOfOrNull(1))
        assertNull(ll.firstIndexOfOrNull(2))

        ll = (1..4).toLinkedList()
        assertEquals(ll.firstIndex(), ll.firstIndexOfOrNull(1))
        assertEquals(ll.firstIndex().next(), ll.firstIndexOfOrNull(2))
        assertEquals(ll.firstIndex().next(2), ll.firstIndexOfOrNull(3))
        assertEquals(ll.lastIndex(), ll.firstIndexOfOrNull(4))
        assertNull(ll.firstIndexOfOrNull(100))

        ll = listOf(1, 1, 2, 2, 3, 4).toLinkedList()
        assertEquals(ll.firstIndex(), ll.firstIndexOfOrNull(1))
        assertEquals(ll.firstIndex().next(2), ll.firstIndexOfOrNull(2))
    }

    @Test
    fun retainAll() {
        var ll = listOf(1, 1, 2, 2, 3, 4).toLinkedList()
        assertTrue(ll.retainAll(listOf(2,3,300)))
        assertEquals(listOf(2, 2, 3), ll.toList())

        ll = listOf(1, 1, 2, 2, 3, 4).toLinkedList()
        assertTrue(ll.retainAll(listOf(1,4)))
        assertEquals(listOf(1,1,4), ll.toList())
    }

    @Test
    fun removeAll() {
        var ll = listOf(1, 1, 2, 2, 3, 4).toLinkedList()
        assertTrue(ll.removeAll(listOf(2,3, 500)))
        assertEquals(listOf(1, 1, 4), ll.toList())

        ll = listOf(1, 1, 2, 2, 3, 4).toLinkedList()
        assertTrue(ll.removeAll(listOf(1,4)))
        assertEquals(listOf(2,2,3), ll.toList())

        ll = listOf(1, 1, 2, 2, 3, 4).toLinkedList()
        assertTrue(ll.removeAll(listOf(1,2,3,4)))
        assertTrue(ll.isEmpty())

        ll = listOf(1, 1, 2, 2, 3, 4).toLinkedList()
        assertFalse(ll.removeAll(listOf(5, 6)))
        assertEquals(listOf(1, 1, 2, 2, 3, 4), ll.toList())
    }

    @Test
    fun clear() {
        val ll = listOf(1, 1, 2, 2, 3, 4).toLinkedList()
        ll.clear()
        assertTrue(ll.isEmpty())
    }

    @Test
    fun addAll() {
        var ll = listOf(1, 2, 3, 4).toLinkedList()
        assertTrue(ll.addAll(listOf(5, 6, 7)))
        assertEquals(listOf(1,2,3,4,5,6,7), ll.toList())

        ll = emptyLinkedList()
        assertTrue(ll.addAll(listOf(5, 6, 7)))
        assertEquals(listOf(5,6,7), ll.toList())
    }

    @Test
    fun containsAll() {
        val ll = listOf(1,2,3,1).toLinkedList()
        assertTrue(ll.containsAll(listOf(1,2)))
        assertTrue(ll.containsAll(listOf(1,1)))
        assertTrue(ll.containsAll(listOf(1,1,1,1,1)))
        assertFalse(ll.containsAll(listOf(4)))
        assertFalse(ll.containsAll(listOf(1,4)))
    }

    @Test
    fun nodePointerTest() {
        val ll = listOf(1,2,3,4).toLinkedList()
        var p = ll.firstIndex()
        assertTrue(ll.hasPointer(p))
        assertTrue(ll.hasPointer(p.next(1)))
        assertTrue(ll.hasPointer(p.next(2)))
        assertTrue(ll.hasPointer(p.next(3)))
        assertFalse(ll.hasPointer(p.next(4)))

        p = ll.lastIndex()
        assertTrue(ll.hasPointer(p))
        assertTrue(ll.hasPointer(p.prev(1)))
        assertTrue(ll.hasPointer(p.prev(2)))
        assertTrue(ll.hasPointer(p.prev(3)))
        assertFalse(ll.hasPointer(p.prev(4)))

        p = ll.firstIndex().prev()
        assertFalse(ll.hasPointer(p))
        p = ll.lastIndex().next()
        assertFalse(ll.hasPointer(p))

        repeat(10) { p = p.next() }
        assertFalse(ll.hasPointer(p))


    }

    @Test
    operator fun iterator() {
        val ll = listOf(1,2,3,4).toLinkedList()
        assertTrue(ll.any { it==2 })
        assertTrue(ll.last() == 4)
        assertEquals(listOf(2,4), ll.filter{it % 2 == 0}.toList())

        val mutableIterator = ll.iterator()
        while (mutableIterator.hasNext()) {
            val value = mutableIterator.next()
            if (value % 2 == 0)
                mutableIterator.remove()
        }
        assertEquals(listOf(1,3), ll.toList())

        val mutableIterator2 = ll.iterator()
        while (mutableIterator2.hasNext()) {
            val value = mutableIterator2.next()
            mutableIterator2.remove()
        }
        assertEquals(emptyList<Int>(), ll.toList())
    }


    @Test
    fun functionsTest() {
        val ll = listOf(1,2,3,4).toLinkedList()
        val ll2 = linkedListOf(1,2,3,4)
        assertEquals(ll.toString(), ll2.toString())
    }


}