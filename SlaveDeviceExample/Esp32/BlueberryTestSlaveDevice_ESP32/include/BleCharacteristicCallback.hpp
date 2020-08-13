#pragma once
#ifndef BLE_CHARACTERISTIC_CALLBACK_HPP
#define BLE_CHARACTERISTIC_CALLBACK_HPP

#include <BLEServer.h>
#include <functional>

class BleCharacteristicCallback : public BLECharacteristicCallbacks {

    public :

        std::function<void(void)> onReadCallback;
        std::function<void(void)> onWriteCallback;

        BleCharacteristicCallback* setOnRead(std::function<void(void)> onReadCallback);
        BleCharacteristicCallback* setOnWrite(std::function<void(void)> onWriteCallback);

        void onRead(BLECharacteristic *bleCharacteristic);
        void onWrite(BLECharacteristic *bleCharacteristic);

};

#endif