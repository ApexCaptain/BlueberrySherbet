#pragma once
#ifndef BLE_MANAGER_HPP
#define BLE_MANAGER_HPP

#include <string>
#include <BLEDevice.h>
#include <BLEUtils.h>

class BleManager {

    public :
        static BleManager* getInstance();
        void startAdvertising();

    private :
        static BleManager* instance;
        BLEServer* bleServer;
        BleManager();

};

#endif