package com.stats4perf;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * Hello world!
 * 
 */
public class AppSigar {
	public static void main(String[] args) {

		Sigar sigar = new Sigar();
		Cpu cpu;
		try {
			cpu = sigar.getCpu();
			System.out.println("idle: " + cpu.getIdle());// get overall CPU idle
			System.out.println("irq: " + cpu.getIrq());
			System.out.println("nice: " + cpu.getNice());
			System.out.println("soft irq: " + cpu.getSoftIrq());
			System.out.println("stolen: " + cpu.getStolen());
			System.out.println("sys: " + cpu.getSys());
			System.out.println("total: " + cpu.getTotal());
			System.out.println("user: " + cpu.getUser());
			System.out.println();

		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CpuPerc perc;
		try {
			perc = sigar.getCpuPerc();
			System.out.println("overall CPU usage");
			System.out.println("system idle: " + perc.getIdle());// get current
																	// CPU idle
																	// rate
			System.out.println("conbined: " + perc.getCombined());// get current
																	// CPU usage

		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
