#include <BleCharacteristicCallback.hpp>

BleCharacteristicCallback* BleCharacteristicCallback::setOnRead(std::function<void(void)> onReadCallback) {
    this -> onReadCallback = onReadCallback;
    return this;
}

BleCharacteristicCallback* BleCharacteristicCallback::setOnWrite(std::function<void(void)> onWriteCallback) {
    this -> onWriteCallback = onWriteCallback;
    return this;
}

void BleCharacteristicCallback::onRead(BLECharacteristic* bleCharacteristic) {
    if(this -> onReadCallback != nullptr) this -> onReadCallback();
}

void BleCharacteristicCallback::onWrite(BLECharacteristic* bleCharacteristic) {
    if(this -> onWriteCallback != nullptr) this -> onWriteCallback();
}
