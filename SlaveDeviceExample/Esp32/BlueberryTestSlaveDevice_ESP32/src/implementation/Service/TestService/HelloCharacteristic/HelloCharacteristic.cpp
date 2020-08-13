#include <Service/TestService/HelloCharacteristic/HelloCharacteristic.hpp>
#include <BleCharacteristicCallback.hpp>

#include <Arduino.h>

HelloCharacteristic* HelloCharacteristic::instance = nullptr;
HelloCharacteristic* HelloCharacteristic::getInstance(BLEService *bleService) {
    if(instance == nullptr) instance = new HelloCharacteristic(bleService);
    return instance;
}

HelloCharacteristic::HelloCharacteristic(BLEService *bleService) {
    bleCharacteristic = bleService -> createCharacteristic(
        CHARACTERISTIC_UUID,
        BLECharacteristic::PROPERTY_WRITE
        | BLECharacteristic::PROPERTY_READ
    );
    
    bleCharacteristic -> setCallbacks(
        (new BleCharacteristicCallback())
            -> setOnWrite([this](){
                Serial.println(bleCharacteristic ->getValue().c_str());
            })
            -> setOnRead([this](){
                bleCharacteristic -> setValue("Hello! Nice to meet you too!");
            })
    );
    
}