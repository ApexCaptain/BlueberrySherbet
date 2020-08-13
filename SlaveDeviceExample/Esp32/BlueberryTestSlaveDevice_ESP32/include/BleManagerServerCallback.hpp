
#pragma once
#ifndef BLE_MANAGER_SERVER_CALLBACK_HPP
#define BLE_MANAGER_SERVER_CALLBACK_HPP

#include <BLEServer.h>
#include <functional>

class BleManagerServerCallback : public BLEServerCallbacks {

    public :
        BleManagerServerCallback(std::function<void(void)> onConnectCallback, std::function<void(void)> onDisconnectCallback);
        void onConnect(BLEServer *bleServer);
        void onDisconnect(BLEServer *bleServer);

    private :
        std::function<void(void)> onConnectCallback;
        std::function<void(void)> onDisconnectCallback;

};

#endif