#include <BleManager.hpp>
#include <BleManagerServerCallback.hpp>

#include <Arduino.h>

#include <Service/TestService/TestService.hpp>

BleManager* BleManager::instance = nullptr;
BleManager* BleManager::getInstance() {
    if(instance == nullptr) instance = new BleManager();
    return instance;
}

void BleManager::startAdvertising() {
    BLEDevice::startAdvertising();
}

BleManager::BleManager() {
    
    // Set basic ble info and connection callbacks
    const std::string advertisingName = "MyDevice";
    BLEDevice::init(advertisingName);
    bleServer = BLEDevice::createServer();
    bleServer -> setCallbacks(new BleManagerServerCallback(
        [](){
            Serial.println("BLE Server Connected...");
        },
        [](){
            Serial.println("BLE Server Disconnected...");
        }
    ));

    // Set services
    TestService *testService = TestService::getInstance(bleServer);

    BLEAdvertising *bleAdvertising = BLEDevice::getAdvertising();
    bleAdvertising -> addServiceUUID(testService -> SERVICE_UUID);
    bleAdvertising -> setScanResponse(true);
    bleAdvertising -> setMinPreferred(0x06);
    
}