#include <Arduino.h>

#include <BleManager.hpp>

BleManager *bleManager;

void setup() {
  Serial.begin(115200);
  bleManager = BleManager::getInstance();
  bleManager -> startAdvertising();
}

void loop() {}