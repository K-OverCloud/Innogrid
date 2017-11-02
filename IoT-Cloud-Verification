package com.innogrid.overcloud.dna.net.worker;

import com.innogrid.overcloud.common.Configuration;
import com.innogrid.overcloud.common.Constants.CMDRESULT;
import com.innogrid.overcloud.common.util.InfluxDBDriver;
import com.innogrid.overcloud.common.util.MibDriver;
import com.innogrid.overcloud.common.Output;
import com.innogrid.overcloud.common.Utils;
import com.innogrid.overcloud.common.model.LoadbalancerStatInfo;
import com.innogrid.overcloud.common.model.NetMeterInfo;
import com.innogrid.overcloud.common.model.TenantInfo;
import com.innogrid.overcloud.dna.net.common.NetUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.innogrid.overcloud.common.Constants.GB;
import static com.innogrid.overcloud.common.Constants.IBPS;
import static com.innogrid.overcloud.common.Constants.IBYTES;
import static com.innogrid.overcloud.common.Constants.KB;
import static com.innogrid.overcloud.common.Constants.LB_ACT;
import static com.innogrid.overcloud.common.Constants.LB_CONNS;
import static com.innogrid.overcloud.common.Constants.LB_CONNSPERSEC;
import static com.innogrid.overcloud.common.Constants.LB_IBYTES;
import static com.innogrid.overcloud.common.Constants.LB_IBYTESPERSEC;
import static com.innogrid.overcloud.common.Constants.LB_INACT;
import static com.innogrid.overcloud.common.Constants.LB_IPACKETS;
import static com.innogrid.overcloud.common.Constants.LB_IPACKETSPERSEC;
import static com.innogrid.overcloud.common.Constants.LB_OBYTES;
import static com.innogrid.overcloud.common.Constants.LB_OBYTESPERSEC;
import static com.innogrid.overcloud.common.Constants.LB_OPACKETS;
import static com.innogrid.overcloud.common.Constants.LB_OPACKETSPERSEC;
import static com.innogrid.overcloud.common.Constants.MB;
import static com.innogrid.overcloud.common.Constants.MONITORING_CYCLE;
import static com.innogrid.overcloud.common.Constants.OBPS;
import static com.innogrid.overcloud.common.Constants.OBYTES;
import static com.innogrid.overcloud.common.Constants.PB;
import static com.innogrid.overcloud.common.Constants.TB;

/**
 * @author
 * @date
 * @brief IoT 클라우드 네트워크 성능 점검 에이전트
 */

public class NetPerformCheck implements Runnable {
  private static Logger logger = LoggerFactory.getLogger(NetPerformCheck.class);
  private Map<String, NetMeterInfo> previousMeterMap = null;
  private MibDriver mibDriver = null;
  private InfluxDBDriver influxDb = null;
  private NetUtils netUtils = null;

  public NetPerformCheck(Configuration config) {
    logger.info("NetPerformCheck Start");
    this.mibDriver = new MibDriver(config);
    this.influxDb = new InfluxDBDriver(config);
    this.netUtils = new NetUtils(config);
    this.previousMeterMap = new HashMap<>();
  }

  private NetMeterInfo getTenantMeter(String VMId) {
    try {
      String[] command = new String[]{"/sbin/ip", "netns", "exec", VMId, "/bin/grep", "-v",
          "Receive\\|packet\\|lo\\:\\|" + VMId, "/proc/net/dev" // get all except Tenant network
      };
      Output result = new Output();
      CMDRESULT returnCode = Utils.runCommand(command, result);
      if (returnCode != CMDRESULT.SUCCESS)
        return null;
      NetMeterInfo meterInfo = new NetMeterInfo();
      for (String line : result.getText().split("\\n")) {

        String[] tokens = line.split("\\s+");
        meterInfo.setReceivedBytes(meterInfo.getReceivedBytes() + Long.parseLong(tokens[1]));
        meterInfo.setReceivedPackets(meterInfo.getReceivedPackets() + Long.parseLong(tokens[2]));
        meterInfo.setTransmittedBytes(meterInfo.getTransmittedBytes() + Long.parseLong(tokens[9]));
        meterInfo
            .setTransmittedPackets(meterInfo.getTransmittedPackets() + Long.parseLong(tokens[10]));
      }
      return meterInfo;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    return null;
  }

  private Map<String, LoadbalancerStatInfo> getTenantLoadbalancerStatList(String VMId) {
    try {
      String[] command =
          new String[]{"/bin/sh", "-c", "/sbin/ip netns exec " + VMId + " /sbin/ipvsadm -Ln"};
      Output result = new Output();
      Map<String, LoadbalancerStatInfo> returnMap = new HashMap<>();
      if (Utils.runCommand(command, result) != CMDRESULT.SUCCESS)
        return returnMap;

      command = new String[]{"/bin/sh", "-c",
          "/sbin/ip netns exec " + VMId + " /sbin/ipvsadm -Ln --rate" // get all lvs info
      };
      Output result2 = new Output();
      if (Utils.runCommand(command, result2) != CMDRESULT.SUCCESS)
        return returnMap;

      command = new String[]{"/bin/sh", "-c",
          "/sbin/ip netns exec " + VMId + " /sbin/ipvsadm -Ln --stats" // get all lvs info
      };
      Output result3 = new Output();
      if (Utils.runCommand(command, result3) != CMDRESULT.SUCCESS)
        return returnMap;


      String[] lines = result.getText().split("\\n");
      int linesNum = lines.length;
      int index = 0;

      while (index < linesNum) {
        String line = lines[index];
        index++;
        if (!line.contains("TCP") && !line.contains("UDP"))
          continue;
        String[] tokens = line.split("\\s+");
        String[] ipPort = tokens[1].split("\\:");
        String ip = ipPort[0];
        int port = Integer.parseInt(ipPort[1]);
        LoadbalancerStatInfo statInfo = new LoadbalancerStatInfo();
        statInfo.setVMId(VMId);
        statInfo.setIp(ip);
        statInfo.setPort(port);
        int actConn = 0;
        int inactConn = 0;
        while (index < linesNum) {
          String subLine = lines[index];
          index++;
          if (!subLine.contains("->")) {
            index--;
            break;
          }
          String[] subTokens = subLine.split("\\s+");

          actConn += Integer.parseInt(subTokens[5]);
          inactConn += Integer.parseInt(subTokens[6]);
        }
        statInfo.setActiveConn(actConn);
        statInfo.setInactiveConn(inactConn);
        returnMap.put(ip + "/" + port, statInfo);
      }

      lines = result2.getText().split("\\n");
      linesNum = lines.length;
      index = 0;
      // rate
      while (index < linesNum) {
        String line = lines[index];
        index++;
        if (!line.contains("TCP") && !line.contains("UDP"))
          continue;
        String[] tokens = line.split("\\s+");
        String[] ipPort = tokens[1].split("\\:");
        String ip = ipPort[0];
        int port = Integer.parseInt(ipPort[1]);
        LoadbalancerStatInfo statInfo = returnMap.get(ip + "/" + port);
        if (statInfo == null) {
          statInfo = new LoadbalancerStatInfo();
          statInfo.setVMId(VMId);
          statInfo.setIp(ip);
          statInfo.setPort(port);
        }
        statInfo.setConnsPerSec(convertUnit(tokens[2]));
        statInfo.setIncomingPktsPerSec(convertUnit(tokens[3]));
        statInfo.setOutgoingPktsPerSec(convertUnit(tokens[4]));
        statInfo.setIncomingBytesPerSec(convertUnit(tokens[5]));
        statInfo.setOutgoingBytesPerSec(convertUnit(tokens[6]));
        returnMap.put(ip + "/" + port, statInfo);
      }

      lines = result3.getText().split("\\n");
      linesNum = lines.length;
      index = 0;
      // rate
      while (index < linesNum) {
        String line = lines[index];
        index++;
        if (!line.contains("TCP") && !line.contains("UDP"))
          continue;
        String[] tokens = line.split("\\s+");
        String[] ipPort = tokens[1].split("\\:");
        String ip = ipPort[0];
        int port = Integer.parseInt(ipPort[1]);
        LoadbalancerStatInfo statInfo = returnMap.get(ip + "/" + port);
        if (statInfo == null) {
          statInfo = new LoadbalancerStatInfo();
          statInfo.setVMId(VMId);
          statInfo.setIp(ip);
          statInfo.setPort(port);
        }
        statInfo.setTotalConns(convertUnit(tokens[2]));
        statInfo.setIncomingPackets(convertUnit(tokens[3]));
        statInfo.setOutgoingPackets(convertUnit(tokens[4]));
        statInfo.setIncomingBytes(convertUnit(tokens[5]));
        statInfo.setOutgoingBytes(convertUnit(tokens[6]));
        returnMap.put(ip + "/" + port, statInfo);
      }

      return returnMap;
    } catch (Exception e) {
      logger.error(e.getLocalizedMessage());
    }
    return new HashMap<>();
  }

  private void writeInfluxDb(NetMeterInfo meterInfo) {
    logger.debug("write net_stat into influxDB");
    String measurement = "net_stat";

    HashMap<String, String> tagSet = new HashMap<>();
    tagSet.put("tenant_id", meterInfo.getVMId());
    tagSet.put("type", "dna");

    HashMap<String, Object> fieldSet = new HashMap<>();
    fieldSet.put(IBPS, meterInfo.getInBPS());
    fieldSet.put(OBPS, meterInfo.getOutBPS());
    fieldSet.put(IBYTES, meterInfo.getiBytes());
    fieldSet.put(OBYTES, meterInfo.getoBytes());

    influxDb.write(measurement, tagSet, fieldSet);
  }

  private void writeInfluxDb(LoadbalancerStatInfo lbStatInfo) {
    logger.debug("write lb_stat into influxDB");
    String measurement = "lb_stat";

    HashMap<String, String> tagSet = new HashMap<>();
    tagSet.put("id", lbStatInfo.getLbId());
    tagSet.put("tenant_id", lbStatInfo.getVMId());
    tagSet.put("port", Integer.toString(lbStatInfo.getPort()));

    HashMap<String, Object> fieldSet = new HashMap<>();
    fieldSet.put(LB_ACT, lbStatInfo.getActiveConn());
    fieldSet.put(LB_INACT, lbStatInfo.getInactiveConn());

    fieldSet.put(LB_CONNS, lbStatInfo.getTotalConns());
    fieldSet.put(LB_IPACKETS, lbStatInfo.getIncomingPackets());
    fieldSet.put(LB_OPACKETS, lbStatInfo.getOutgoingPackets());
    fieldSet.put(LB_IBYTES, lbStatInfo.getIncomingBytes());
    fieldSet.put(LB_OBYTES, lbStatInfo.getOutgoingBytes());

    fieldSet.put(LB_CONNSPERSEC, lbStatInfo.getConnsPerSec());
    fieldSet.put(LB_IPACKETSPERSEC, lbStatInfo.getIncomingPktsPerSec());
    fieldSet.put(LB_OPACKETSPERSEC, lbStatInfo.getOutgoingPktsPerSec());
    fieldSet.put(LB_IBYTESPERSEC, lbStatInfo.getIncomingBytesPerSec());
    fieldSet.put(LB_OBYTESPERSEC, lbStatInfo.getOutgoingBytesPerSec());

    influxDb.write(measurement, tagSet, fieldSet);
  }

  private long convertUnit(String s) {
    long l;
    if (s.contains("K")) {
      l = Long.parseLong(s.split("K")[0]) * KB;
    } else if (s.contains("M")) {
      l = Long.parseLong(s.split("M")[0]) * MB;
    } else if (s.contains("G")) {
      l = Long.parseLong(s.split("G")[0]) * GB;
    } else if (s.contains("T")) {
      l = Long.parseLong(s.split("T")[0]) * TB;
    } else if (s.contains("P")) {
      l = Long.parseLong(s.split("P")[0]) * PB;
    } else {
      l = Long.parseLong(s);
    }
    return l;
  }

  private void checkPerformSflowrt(String VMId) {
    try {
      int pCount = netUtils.checkPerformSflowrt(VMId);
      if (pCount == 0) {
        netUtils.start(VMId);
      } else if (pCount > 1) {
        logger.warn("network namespace sflow-rt performance is duplicated!");
        netUtils.stop(VMId);
        netUtils.start(VMId);
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }


  @Override
  public void run() {

    try {
      for (TenantInfo tenantInfo : mibDriver
          .getTenantInfoListByNetNodeIp(Utils.getMgmtInterfaceIp())) {
        String VMId = tenantInfo.getId();
        for (LoadbalancerStatInfo statInfo : getTenantLoadbalancerStatList(VMId).values()) {
          logger.debug("write lb stat info ip=" + statInfo.getIp() + " port=" + statInfo.getPort());
          String lbId = mibDriver.getLbIdByIpAndPort(statInfo.getIp(), statInfo.getPort(), VMId);
          if (lbId == null) {
            logger.error("cannot find lb Ip");
            continue;
          }
          statInfo.setLbId(lbId);
          writeInfluxDb(statInfo);
        }

        logger.debug("getting metering info of tenant: " + VMId);
        NetMeterInfo meterInfo = getTenantMeter(VMId);
        if (meterInfo == null) {
          logger.error("Cannot get metering info of tenant " + VMId);
          // continue;
          meterInfo = new NetMeterInfo();
          meterInfo.setVMId(VMId);
          meterInfo.setInBPS(0L);
          meterInfo.setOutBPS(0L);
          meterInfo.setiBytes(0L);
          meterInfo.setoBytes(0L);
          writeInfluxDb(meterInfo);
          previousMeterMap.remove(VMId);
        } else {
          if (previousMeterMap.containsKey(VMId)) {
            NetMeterInfo previousMeter = previousMeterMap.get(VMId);
            if (previousMeter.getReceivedBytes() <= meterInfo.getReceivedBytes()
                && previousMeter.getTransmittedBytes() <= meterInfo.getTransmittedBytes()) {

              Long iBytes = meterInfo.getReceivedBytes() - previousMeter.getReceivedBytes();
              Long oBytes = meterInfo.getTransmittedBytes() - previousMeter.getTransmittedBytes();

              logger
                  .debug("previous metering data is valid. iBytes=" + iBytes + ",oBytes=" + oBytes);

              Long inBPS = iBytes / (MONITORING_CYCLE / 1000) * 8;
              Long outBPS = oBytes / (MONITORING_CYCLE / 1000) * 8;

              meterInfo.setVMId(VMId);
              meterInfo.setOutBPS(outBPS);
              meterInfo.setInBPS(inBPS);
              meterInfo.setiBytes(iBytes);
              meterInfo.setoBytes(oBytes);
              writeInfluxDb(meterInfo);
            }
          }
          previousMeterMap.put(VMId, meterInfo);
        }

        checkSflowrt(VMId);
      }
    } catch (Exception e) {
      logger.error("Net metering failed : " + e.getMessage(), e);
    }
  }

}
