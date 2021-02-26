#include <Arduino.h>

#include <BleManager.hpp>

BleManager *bleManager;

void setup() {
  Serial.begin(115200);
  bleManager = BleManager::getInstance();
  bleManager -> startAdvertising();
  Serial.printf("ESP_BLE_PWR_TYPE_DEFAULT=%d \n", ::esp_ble_tx_power_get(ESP_BLE_PWR_TYPE_DEFAULT)); 
  Serial.printf("ESP_BLE_PWR_TYPE_ADV =%d \n", ::esp_ble_tx_power_get(ESP_BLE_PWR_TYPE_ADV));
  Serial.printf("ESP_BLE_PWR_TYPE_SCAN  =%d \n", ::esp_ble_tx_power_get(ESP_BLE_PWR_TYPE_SCAN));
}

void loop() {}