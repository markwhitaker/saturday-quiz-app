package uk.co.mainwave.saturdayquizapp.mvp

/**
 * Base interface for an MVP view
 */
interface MvpView

/**
 * Base interface for an MVP presenter, specifying [V] as the [MvpView] type.
 */
abstract class MvpPresenter<V : MvpView> {
    protected lateinit var view: V

    /**
     * Attach the [view] to the presenter once the view is constructed
     */
    fun onViewCreated(view: V) {
        this.view = view
    }

    /**
     * Inform the presenter that the view has been displayed
     */
    abstract fun onViewDisplayed()
}
