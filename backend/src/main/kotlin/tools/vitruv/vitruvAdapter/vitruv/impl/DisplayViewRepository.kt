package tools.vitruv.vitruvAdapter.vitruv.impl

import tools.vitruv.vitruvAdapter.vitruv.api.DisplayView
import tools.vitruv.vitruvAdapter.vitruv.api.DisplayViewContainer

class DisplayViewRepository : DisplayViewContainer {

    private val displayViews = mutableSetOf<DisplayView>()

    override fun registerDisplayView(displayView: DisplayView) {
        displayViews.add(displayView)
    }

    override fun registerDisplayViews(displayViews: Set<DisplayView>) {
        this.displayViews.addAll(displayViews)
    }

    override fun getDisplayViews(): Set<DisplayView> {
        return displayViews.toSet()
    }

    override fun getDisplayView(name: String): DisplayView? {
        return displayViews.find { it.name == name }
    }
}
