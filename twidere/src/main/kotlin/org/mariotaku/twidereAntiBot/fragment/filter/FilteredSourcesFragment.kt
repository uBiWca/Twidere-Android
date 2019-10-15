package org.mariotaku.twidereAntiBot.fragment.filter

import android.net.Uri
import org.mariotaku.twidereAntiBot.provider.TwidereDataStore.Filters

class FilteredSourcesFragment : BaseFiltersFragment() {

    override val contentColumns: Array<String> = Filters.Sources.COLUMNS

    override val contentUri: Uri = Filters.Sources.CONTENT_URI

}