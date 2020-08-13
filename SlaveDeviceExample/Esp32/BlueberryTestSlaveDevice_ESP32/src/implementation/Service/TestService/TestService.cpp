#include <Service/TestService/TestService.hpp>
#include <Service/TestService/HelloCharacteristic/HelloCharacteristic.hpp>

TestService* TestService::instance = nullptr;
TestService* TestService::getInstance(BLEServer *bleServer) {
    if(instance == nullptr) instance = new TestService(bleServer);
    return instance;
}

TestService::TestService(BLEServer *bleServer) {
    bleService = bleServer -> createService(SERVICE_UUID);
    HelloCharacteristic* helloCharacteristic = HelloCharacteristic::getInstance(bleService);
    bleService -> start();
}