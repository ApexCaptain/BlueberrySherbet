#pragma once
#ifndef TEST_SERVICE_HPP
#define TEST_SERVICE_HPP

#include <BLEServer.h>
#include <string>

class TestService {

    public :
        static TestService *getInstance(BLEServer *bleServer);
        const std::string SERVICE_UUID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0100";
        BLEService *bleService;

    private :
        static TestService* instance;
        TestService(BLEServer *bleServer);

};

#endif