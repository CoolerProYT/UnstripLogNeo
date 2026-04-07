package com.coolerpromc.unstriplog;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	public static final String MODID = "unstriplog";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static Identifier id(String name){
		return Identifier.fromNamespaceAndPath(MODID, name);
	}
}