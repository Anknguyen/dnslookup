package com.met.dnslookup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class DnslookupApplication implements CommandLineRunner
{

	public static void main(String[] args) {
		SpringApplication.run(DnslookupApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Test DNS ... ");
		dnslookup(new String[] {"google.com"});
	}

	public static void dnslookup(String args[]) {
	// explain what program does and how to use it
	if (args.length != 1) {
		System.err.println("Print out DNS Record for an Internet Address");
		System.err.println("USAGE: java DNSLookup domainName|domainAddress");
		System.exit(-1);
	}
	try {
		InetAddress inetAddress;
		// if first character is a digit then assume is an address
		if (Character.isDigit(args[0].charAt(0))) {
			// convert address from string representation to byte array
			byte[] b = new byte[4];
			String[] bytes = args[0].split("[.]");
			for (int i = 0; i < bytes.length; i++) {
				b[i] = Integer.valueOf(bytes[i]).byteValue();
			}
			// get Internet Address of this host address
			inetAddress = InetAddress.getByAddress(b);
		}
		else {
			// get Internet Address of this host name
			inetAddress = InetAddress.getByName(args[0]);
		}
		// show the Internet Address as name/address
		System.out.println(inetAddress.getHostName() + "/" + inetAddress.getHostAddress());
		// get the default initial Directory Context
		InitialDirContext iDirC = new InitialDirContext();
		// get all the DNS records for inetAddress
		Attributes attributes =
				iDirC.getAttributes("dns:/" + inetAddress.getHostName(), new String[] {"*"});
		// get an enumeration of the attributes and print them out
		NamingEnumeration<? extends Attribute> attributeEnumeration = attributes.getAll();
		System.out.println("-- DNS INFORMATION --");
		while (attributeEnumeration.hasMore()) {
			Attribute attribute = attributeEnumeration.next();
			//System.out.println("ID = " + attribute.getID());
			//System.out.println("get = " + attribute.get());
			System.out.println("" + attribute);
		}
	}
	catch (UnknownHostException exception) {
		System.err.println("ERROR: No Internet Address for '" + args[0] + "'");
	}
	catch (NamingException exception) {
		System.err.println("ERROR: No DNS record for '" + args[0] + "'");
	}
}
}
