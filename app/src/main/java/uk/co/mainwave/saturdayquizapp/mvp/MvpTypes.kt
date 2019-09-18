package uk.co.mainwave.saturdayquizapp.mvp

/**
 * Base interface for an MVP view
 */
interface MvpView

/**
 * Base interface for an MVP presenter, specifying [T] as the [MvpView] type.
 */
abstract class MvpPresenter<T : MvpView> {
    protected lateinit var view: T

    /**
     * Attach the [view] to the presenter once the view is constructed
     */
    fun onViewCreated(view: T) {
        this.view = view
    }

    /**
     * Inform the presenter that the view has been displayed
     */
    abstract fun onViewDisplayed()
}
