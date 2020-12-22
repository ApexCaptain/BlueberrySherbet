package com.gmail.ayteneve93.blueberryshertbettestapplication.movement

import com.gmail.ayteneve93.blueberrysherbetannotations.BlueberryService
import com.gmail.ayteneve93.blueberrysherbetannotations.READ
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryReadRequestInfo

@BlueberryService
interface MovementService {

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee101")
    fun testRead() : BlueberryReadRequestInfo<String>

}