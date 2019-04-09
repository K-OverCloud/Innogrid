# OverCloud Monitoring System

## Overview ##
OverCloud Monitoring is a system for monitoring and verifying various SaaS services (3tier, IoT, BigData) on the overcloud.

## Requirements
Interface Proxy

* Telegraf
* InfluxDB
* Ubuntu 16.04

## Install
Execute script for agent installation for data collection of monitoring system

```
$ git clone https://github.com/K-OverCloud/SaaS-Compatibility-Verification.git
$ cd VerifOverCloud
```

Change "10.10.10.10" in Bind_Address to Monitoring System IP(InfluxDB Server)
```
$ vim install.sh
```

Installation Start
```
$ ./install.sh
```

## Running
Service Running Check
```
$ service start telegraf
```
