#include <BleManagerServerCallback.hpp>

BleManagerServerCallback::BleManagerServerCallback(std::function<void(void)> onConnectCallback, std::function<void(void)> onDisconnectCallback) {
    this -> onConnectCallback = onConnectCallback;
    this -> onDisconnectCallback = onDisconnectCallback;
}

void BleManagerServerCallback::onConnect(BLEServer *bleServer) {
    this -> onConnectCallback();
}

void BleManagerServerCallback::onDisconnect(BLEServer *bleServer) {
    this -> onDisconnectCallback();
}
