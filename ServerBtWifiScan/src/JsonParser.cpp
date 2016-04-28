/*
 * JsonParser.cpp
 *
 *  Created on: 28.4.2016
 *      Author: arska
 */

#include "JsonParser.h"
#include <vector>
#include <sstream>
#include <stdio.h>
#include <string.h>
#include "Btresult.h"
#include "Wifiresult.h"

char		btInitialJsonParse[] = "{\"btscans\":[%[^]]%n", wifiInitialJsonParse[] = "{\"wifiscans\":[%[^]]";
char		btJsonParse[] = "\"devName\":\"%[^\"]\", \"devAddr\":\"%[^\"]\", \"devType\":\"%[^\"]\", \"devRssi\":\"%[^\"]\", \"location\":\"%[^\"]\"";
char		wifiJsonParse[] = "\"ssid\":\"%[^\"]\", \"bssid\":\"%[^\"]\", \"capabilities\":\"%[^\"]\", \"rssi\":\"%[^\"]\", \"freq\":\"%[^\"]\", \"location\":\"%[^\"]\"";
char		btjson[2048], btdevjson[1024], devName[64], devAddr[64], devType[64], devRssi[64], btloc[64];
char		wifijson[2048], wifinetjson[1024], ssid[64], bssid[64], capabilities[64], wifiRssi[64], freq[64], wifiloc[64];
int 		offset = 0, pos;
std::vector<Wifiresult> _listWifiNetworks;
std::vector<Btresult> _listBtDevices;

JsonParser::JsonParser() {
	// TODO Auto-generated constructor stub

}
vector<Btresult> JsonParser::parseBtJson(char _buff[]){
	_listBtDevices.clear();
	std::string header;
	std::istringstream req(_buff);
	while (std::getline(req, header)) {
		int readChars = 0;
		sscanf(header.c_str(), btInitialJsonParse, btjson, &readChars); // JSON for all bt results
	}
	offset = 0;
	// Parse btJson into bt results
	while(sscanf(btjson+offset, "{%[^}]%n", btdevjson, &pos)!=0) {
		if(btdevjson[0]==0) break; // TODO: Better way for checking empty
		offset += pos+2;
		if(sscanf(btdevjson, btJsonParse, devName, devAddr, devType, devRssi, btloc) == 5) // All five successful
			_listBtDevices.push_back(Btresult(devName, devAddr, devType, devRssi, btloc)); // Add new bt result
		memset(btdevjson, 0, sizeof(btdevjson));
	}
	memset(btjson, 0, sizeof(btjson));
	return _listBtDevices;
}

vector<Wifiresult> JsonParser::parseWifiJson(char _buff[]){
	_listWifiNetworks.clear();
	std::string header;
	std::istringstream req(_buff);
	while (std::getline(req, header)) {
		int readChars = 0;
		sscanf(header.c_str(), btInitialJsonParse, btjson, &readChars); // JSON for all bt results
		sscanf(header.c_str()+readChars+2, wifiInitialJsonParse, wifijson); // JSON for all wifi results
	}
	// Parse WifiJson into wifi results
	offset = 0;
	while(sscanf(wifijson+offset, "{%[^}]%n", wifinetjson, &pos)!=0){
		if(wifinetjson[0]==0) break; // TODO: Better way for checking empty
		offset += pos+2;
		if(sscanf(wifinetjson, wifiJsonParse, ssid, bssid, capabilities, wifiRssi, freq, wifiloc) == 6) // All six successful
			_listWifiNetworks.push_back(Wifiresult(ssid, bssid, capabilities, wifiRssi, freq, wifiloc)); // Add wifi result
		memset(wifinetjson, 0, sizeof(wifinetjson));
	}
	memset(btjson, 0, sizeof(btjson));
	memset(wifijson, 0, sizeof(wifijson));
	return _listWifiNetworks;
}

JsonParser::~JsonParser() {
	// TODO Auto-generated destructor stub
}
