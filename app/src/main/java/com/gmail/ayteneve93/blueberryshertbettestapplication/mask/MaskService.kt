package com.gmail.ayteneve93.blueberryshertbettestapplication.mask

import com.gmail.ayteneve93.blueberrysherbetannotations.BlueberryService
import com.gmail.ayteneve93.blueberrysherbetannotations.INDICATE
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryNotifyOrIndicateRequestInfo

@BlueberryService
interface MaskService {

    @INDICATE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee102", endSignal = "\$EoD")
    fun indicateFromRPI() : BlueberryNotifyOrIndicateRequestInfo<String>

}