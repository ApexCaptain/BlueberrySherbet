#pragma once
#ifndef HELLO_CHARACTERISTIC_HPP
#define HELLO_CHARACTERISTIC_HPP

#include <BLEServer.h>
#include <BLE2902.h>
#include <string>

class HelloCharacteristic {

    public :
        static HelloCharacteristic* getInstance(BLEService *bleService);
        const std::string CHARACTERISTIC_UUID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101";
        BLECharacteristic *bleCharacteristic;
    
    private :
        static HelloCharacteristic* instance;
        HelloCharacteristic(BLEService *bleService);

};

#endif