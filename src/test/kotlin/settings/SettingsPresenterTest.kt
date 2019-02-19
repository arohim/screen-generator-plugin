package settings

import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class SettingsPresenterTest {

    @Mock
    private lateinit var viewMock: SettingsView

    private lateinit var presenter: SettingsPresenter

    private val testElement = ScreenElement("Test")

    @Before
    fun setUp() {
        presenter = SettingsPresenter(viewMock)
    }

    @Test
    fun `on load view`() {
        val settings = Settings(listOf(testElement))

        presenter.onLoadView(settings)

        verify(viewMock).setUpListeners()
        verify(viewMock).showScreenElements(listOf(testElement))
        assertEquals(listOf(testElement), presenter.screenElements)
        assertEquals(settings, presenter.initialSettings)
    }

    @Test
    fun `on add click`() {
        val element = ScreenElement(UNNAMED_ELEMENT)

        presenter.onAddClick()

        verify(viewMock).addScreenElement(element)
        verify(viewMock).selectLastScreenElement()
        assertTrue(presenter.screenElements.contains(element))
        assertTrue(presenter.isModified)
    }

    @Test
    fun `on delete click`() {
        val element = ScreenElement(UNNAMED_ELEMENT)
        presenter.screenElements.add(element)

        presenter.onDeleteClick(0)

        verify(viewMock).removeScreenElement(0)
        assertTrue(presenter.screenElements.isEmpty())
        assertTrue(presenter.isModified)
    }

    @Test
    fun `when index is in bounds on screen element select`() {
        val index = 0
        presenter.screenElements.add(testElement)

        presenter.onScreenElementSelect(index)

        verify(viewMock).removeCurrentNameChangeListener()
        verify(viewMock).showName("Test")
        verify(viewMock).addNameChangeListener()
        assertEquals(testElement, presenter.currentSelectedScreenElement)
    }

    @Test
    fun `when index is not in bounds on screen element select`() {
        val index = 0

        presenter.onScreenElementSelect(index)

        verify(viewMock).removeCurrentNameChangeListener()
        verify(viewMock).showName("")
        assertEquals(null, presenter.currentSelectedScreenElement)
    }

    @Test
    fun `when current selected screen element is null on name change`() {
        presenter.onNameChange("Test")

        verifyZeroInteractions(viewMock)
        assertFalse(presenter.isModified)
    }

    @Test
    fun `when current selected screen element is not null on name change`() {
        presenter.currentSelectedScreenElement = testElement
        presenter.screenElements.add(testElement)

        presenter.onNameChange("Test Test")

        assertEquals("Test Test", testElement.name)
        verify(viewMock).updateScreenElement(0, testElement)
        assertTrue(presenter.isModified)
    }

    @Test
    fun `on apply settings`() {
        presenter.screenElements.add(testElement)

        presenter.onApplySettings()

        verify(viewMock).updateComponent(Settings(listOf(testElement)))
        assertFalse(presenter.isModified)
        assertEquals(Settings(listOf(testElement)), presenter.initialSettings)
    }

    @Test
    fun `on reset settings`() {
        presenter.initialSettings = Settings(listOf(testElement))

        presenter.screenElements.add(testElement)
        presenter.screenElements.add(testElement)

        presenter.onResetSettings()

        inOrder(viewMock) {
            verify(viewMock).clearScreenElements()
            verify(viewMock).showScreenElements(listOf(testElement))
        }
        assertEquals(listOf(testElement), presenter.screenElements)
        assertFalse(presenter.isModified)
    }

}