package org.mariotaku.twidereAntiBot.fragment.filter

import android.net.Uri
import org.mariotaku.twidereAntiBot.provider.TwidereDataStore.Filters

class FilteredLinksFragment : BaseFiltersFragment() {

    override val contentColumns: Array<String> = Filters.Links.COLUMNS

    override val contentUri: Uri = Filters.Links.CONTENT_URI

}