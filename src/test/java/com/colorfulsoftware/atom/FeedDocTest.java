/**
 * Copyright 2011 Bill Brown
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.colorfulsoftware.atom;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.colorfulsoftware.atom.AtomSpecException;
import com.colorfulsoftware.atom.Attribute;
import com.colorfulsoftware.atom.Author;
import com.colorfulsoftware.atom.Category;
import com.colorfulsoftware.atom.Contributor;
import com.colorfulsoftware.atom.Entry;
import com.colorfulsoftware.atom.Extension;
import com.colorfulsoftware.atom.Feed;
import com.colorfulsoftware.atom.FeedDoc;
import com.colorfulsoftware.atom.FeedWriter;
import com.colorfulsoftware.atom.Generator;
import com.colorfulsoftware.atom.Icon;
import com.colorfulsoftware.atom.Id;
import com.colorfulsoftware.atom.Link;
import com.colorfulsoftware.atom.Logo;
import com.colorfulsoftware.atom.Name;
import com.colorfulsoftware.atom.Rights;
import com.colorfulsoftware.atom.Title;
import com.colorfulsoftware.atom.Updated;

//uncomment stax-utils dependency in the root pom.xml to see exapmle usage.
//import javanet.staxutils.IndentingXMLStreamWriter;

/**
 * This class tests the feed library. See the source code for examples.
 * 
 * @author Bill Brown
 * 
 */
public class FeedDocTest implements Serializable {

	private static final long serialVersionUID = 4141631875438242460L;
	private Feed feed1;
	private FeedDoc feedDoc;

	private static Calendar theDate;
	static {
		theDate = Calendar.getInstance();
		theDate.clear();
		theDate.set(2008, 0, 1);
	}

	private String mega = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\" xmlns:what=\"http://abc.def.ghi.com\" xmlns:local=\"http://purl.org/dc/elements/1.1/fakeNamespace\">"
			+ "<id local:something=\"testVal\">http://colorfulsoftware.localhost/projects/atomsphere/atom.xml</id>"
			+ "<updated local:somethingElse=\"fakeValue\">2007-03-08T20:52:40.70-06:00</updated>"
			+ "<fakeExt xmlns=\"http://www.fake.extension.org/fakeness\" />"
			+ "<div xmlns=\"http://www.w3.org/1999/xhtml\" />"
			+ "<fakeExt xmlns=\"http://www.fake.extension.org/fakeness\">fakecontent</fakeExt>"
			+ "<div xmlns=\"http://www.w3.org/1999/xhtml\">fakecontent</div>"
			+ "<generator uri=\"http://www.colorfulsoftware.com/projects/atomsphere\" version=\"1.0.2.0\"></generator>"
			+ "<title type=\"xhtml\"><div xmlns=\"http://www.w3.org/1999/xhtml\">Atomsphere a <b>great atom 1.0 parser </b></div></title>  <subtitle>a java atom feed library</subtitle>"
			+ "<author local:testAttr=\"testVal\"><test:test xmlns:test=\"http://www.w3.org/1999/test\" /><name>Bill Brown</name><uri>http://www.colorfulsoftware.com</uri><email>info@colorfulsoftware.com</email></author>"
			+ "<contributor><name>Other Brown</name><uri>http://www.colorfulsoftware.com</uri><email>info@colorfulsoftware.com</email></contributor>"
			+ "<contributor local:testAttr=\"testVal\"><test:test xmlns:test=\"http://www.w3.org/1999/test\" /><name>Bill Brown</name><uri>http://www.colorfulsoftware.com</uri><email>info@colorfulsoftware.com</email></contributor>"
			+ "<category term=\"math\" scheme=\"http://www.colorfulsoftware.com/projects/atomsphere/\" label=\"math\" />"
			+ "<category term=\"science\" scheme=\"http://www.colorfulsoftware.com/projects/atomsphere/\" label=\"science\"/>"
			+ "<category term=\"thing\" />"
			+ "<category term=\"undefined\"></category>"
			+ "<category term=\"undefined\">This is valid.  See http://www.atomenabled.org/developers/syndication/atom-format-spec.php The \"atom:category\" element conveys information about a category associated with an entry or feed. This specification assigns no meaning to the content (if any) of this element. (#Other Extensibility section</category>"
			+ "<link href=\"http://www.colorfulsoftware.com/projects/atomsphere/atom.xml\" rel=\"self\" type=\"application/atom+xml\" hreflang=\"en-us\" title=\"cool site\" />"
			+ "<link href=\"http://www.colorfulsoftware.com/projects/atomsphere/extension/sort/1.0/sort.rnc\" rel=\"alternate\" type=\"application/atom+xml\" hreflang=\"en-us\" title=\"relax ng\" >This is also valid.  See http://www.atomenabled.org/developers/syndication/atom-format-spec.php The \"atom:link\" element defines a reference from an entry or feed to a Web resource. This specification assigns no meaning to the content (if any) of this element. (#Other Extensibility section)</link>"
			+ "<icon local:testAttr=\"testVal\">http://www.minoritydirectory.net/images/logo.gif</icon>"
			+ "<logo local:testAttr=\"testVal\">http://www.minoritydirectory.net/images/logo.gif</logo>"
			+ "<rights xmlns=\"http://www.w3.org/2005/Atom\" type=\"html\">Copyright 2007 &lt;hr /&gt;</rights>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\"><id>http://colorfulsoftware.localhost/colorfulsoftware/projects/atomsphere/atom.xml#About</id>"
			+ "<updated>2009-03-02T13:00:00.699-06:00</updated><title>About</title><published xmlns=\"http://www.w3.org/2005/Atom\">2007-02-26T12:34:01.330-06:00</published>"
			+ "<summary>About the project</summary>"
			+ "<author local:testAttr=\"testVal\"><test:test xmlns:test=\"http://www.w3.org/1999/test\" /><name>Bill Brown</name><uri>http://www.colorfulsoftware.com</uri><email>info@colorfulsoftware.com</email></author>"
			+ "<contributor><name>Other Brown</name><uri>http://www.colorfulsoftware.com</uri><email>info@colorfulsoftware.com</email></contributor>"
			+ "<contributor local:testAttr=\"testVal\"><test:test xmlns:test=\"http://www.w3.org/1999/test\" /><name>Bill Brown</name><uri>http://www.colorfulsoftware.com</uri><email>info@colorfulsoftware.com</email></contributor>"
			+ "<rights xmlns=\"http://www.w3.org/2005/Atom\">Copyright 2007</rights>"
			+ "<category term=\"math\" scheme=\"http://www.colorfulsoftware.com/projects/atomsphere/\" label=\"math\" />"
			+ "<category term=\"science\" scheme=\"http://www.colorfulsoftware.com/projects/atomsphere/\" label=\"science\"/>"
			+ "<link href=\"http://www.colorfulsoftware.com/projects/atomsphere/atom.xml\" rel=\"alternate\" type=\"application/atom+xml\" hreflang=\"en-us\" title=\"cool site\" />"
			+ "<content type=\"html\">&lt;ul&gt; &lt;li&gt;&lt;span class=\"boldText\"&gt;Atomsphere&lt;/span&gt; isa java library that allows you to create and modify atom 1.0 feeds.&lt;/li&gt; &lt;li&gt;It is distributed under the GNU GPL license and can be used in any manner complient with the license.&lt;/li&gt; &lt;li&gt;It is also packaged as a servlet-lib for use in web applications.&lt;/li&gt; &lt;li&gt;It is also packaged as a customtag library to display feeds on a webapage.&lt;/li&gt; &lt;li&gt;It also comes with an example webapp which demonstrates some example uses of the library.&lt;/li&gt; &lt;li&gt;It is written to be tied as closely as possible to the current atom specification found &lt;a href=\"http://www.atomenabled.org/developers/syndication/atom-format-spec.php\"&gt;here&lt;/a&gt;.&lt;/li&gt; &lt;/ul&gt;</content>"
			+ "<local:element xmlns:local=\"http://purl.org/dc/elements/1.1/fakeNamespace\">something that is an extension.</local:element>"
			+ "</entry>"
			+ "<entry>"
			+ "<source xmlns:sort=\"http://www.colorfulsoftware.com/projects/atomsphere/extension/sort/1.0\"><id>http://www.minoritydirectory.net/latest.xml</id>"
			+ "<updated>2009-10-20T08:23:27.830-06:00</updated>"
			+ "<generator>What up doh?</generator>"
			+ "<category term=\"purpose\" label=\"Organization Listings for Minorities in the USA\" />"
			+ "<title>Latest Updates...</title><subtitle type=\"html\">A much needed resource.&lt;hr /&gt;</subtitle>"
			+ "<rights>Free Speech</rights>"
			+ "<author><what:now myAttr=\"valuable\" /><name>The Minority Directory</name></author><contributor><name>The People</name></contributor>"
			+ "<link href=\"http://www.minoritydirectory.net/latest.xml\" rel=\"self\" />"
			+ "<icon>http://www.minoritydirectory.net/images/favicon.ico</icon>"
			+ "<logo>http://www.minoritydirectory.net/images/logo.gif</logo>"
			+ "<sort:asc type=\"updated\" />"
			+ "</source><id>http://www.minoritydirectory.net/Virtual+Quest+llc</id>"
			+ "<updated>2009-10-14T15:25:39.00-06:00</updated>"
			+ "<title type=\"html\">&lt;a href=\"http://www.minoritydirectory.net/businessservices\" title=\"See Full Listing...\" >Virtual Quest llc&lt;/a></title>"
			+ "<author><name>Virtual Quest llc</name></author>"
			+ "<link href=\"http://www.minoritydirectory.net/businessservices\" rel=\"alternate\" />"
			+ "<summary type=\"html\">&lt;br />&lt;span class=\"certifications\">Certifications:&lt;/span> &lt;a href=\"http://www.nmsdc.org/nmsdc/\" title=\"The National Minority Supplier Development Council\">MBE&lt;/a> &lt;br />Solutions group for web, windows and software development</summary>"
			+ "</entry>"
			+ "<entry><id>http://abc.net</id><title>dude man</title><updated>2007-03-08T20:52:40.70-06:00</updated><summary>Yah Man</summary><content src=\"http://www.jamaicafunk.net\" /></entry>"
			+ "<entry><id>http://colorfulsoftware.localhost/colorfulsoftware/projects/atomsphere/atom.xml#Requirements</id>"
			+ "<updated>2008-03-02T12:59:54.274-06:00</updated><title>Requirements</title><published>2007-02-26T12:58:53.197-06:00</published>"
			+ "<summary>Requirements for using the libraries</summary>"
			+ "<content type=\"html\">&lt;br /&gt;the project is usable with jdk 1.4.2 and above&lt;br /&gt; &amp;nbsp;&lt;br /&gt; needed for using the library&lt;br /&gt; &lt;ul&gt; &lt;li&gt;&lt;a href=\"https://sjsxp.dev.java.net/\"&gt;jsr173&lt;/a&gt; (STAX api jar) - see the &lt;a href=\"http://jcp.org/aboutJava/communityprocess/final/jsr173/index.html\"&gt;API&lt;/a&gt;.&lt;/li&gt; &lt;li&gt;&lt;a href=\"https://sjsxp.dev.java.net/\"&gt;sjsxp&lt;/a&gt; (STAX implementation) - others implementations may work but have not been tested.&lt;/li&gt; &lt;li&gt;&lt;a href=\"https://stax-utils.dev.java.net/\"&gt;stax-utils&lt;/a&gt; (for pretty printing)&lt;/li&gt; &lt;/ul&gt; needed for using the atomsphere-taglib&lt;br /&gt; &lt;ul&gt; &lt;li&gt;the atomsphere library&lt;/li&gt; &lt;li&gt;Any J2EE Servlet Container&lt;/li&gt; &lt;/ul&gt; needed for using the atomsphere-weblib&lt;br /&gt; &lt;ul&gt; &lt;li&gt;the atomsphere library&lt;/li&gt; &lt;li&gt;Any J2EE Servlet Container&lt;/li&gt; &lt;/ul&gt; needed for using the example atomsphere-webapp&lt;br /&gt; &lt;ul&gt; &lt;li&gt;Any J2EE Servlet Container&lt;/li&gt;&lt;/ul&gt;</content>"
			+ "</entry>"
			+ "<entry xmlns:test=\"http://www.w3.org/1999/test\"><id>http://colorfulsoftware.localhost/colorfulsoftware/projects/atomsphere/atom.xml#Documentation</id>"
			+ "<updated>2007-03-02T12:59:45.475-06:00</updated><title>Documentation</title><published>2007-02-26T13:00:00.478-06:00</published>"
			+ "<summary>Starting Documentation</summary>"
			+ "<test:test xmlns:test=\"http://www.w3.org/1999/test\">this is an extension test <test:does> it work? </test:does> we'll see</test:test>"
			+ "<content type=\"html\">&lt;h4&gt;Installation (atomsphere library)&lt;/h4&gt; &lt;ul&gt; &lt;li&gt;Add the jsr173, sjsxp, stax-utils and atomsphere jars to the classpath (WEB-INF/lib for webapps).&lt;/li&gt; &lt;/ul&gt; &lt;h4&gt;Installation (atomsphere-taglib)&lt;/h4&gt; &lt;ul&gt; &lt;li&gt;Add the atomsphere jar to the classpath (WEB-INF/lib for webapps).&lt;/li&gt;&lt;li&gt;Add the atomsphereTags.tld tag descriptor to the top of the jsp page (See example below). &lt;/li&gt; &lt;li&gt;Add anyrequired attributes and optional attributes to the custom tag (See example below).&lt;/li&gt; &lt;li&gt;View the atomsphereTags.tld for a description of the attributes and what they do.&lt;/li&gt;&lt;/ul&gt; &lt;h4&gt;Installation (atomsphere-weblib)&lt;/h4&gt; &lt;ul&gt;&lt;li&gt;Add the atomsphere and atomsphere-weblib jars to the classpath (WEB-INF/lib for webapps).&lt;/li&gt;&lt;li&gt;Copy the web-fragrment.xml (embeded in the jar file) to your application's web.xml file.&lt;/li&gt;&lt;/ul&gt; &lt;h4&gt;Installation (atomsphere-webapp)&lt;/h4&gt; &lt;ul&gt; &lt;li&gt;Deploy the war file to any J2EE servlet container.&lt;/li&gt; &lt;/ul&gt;</content>"
			+ "</entry>" + "</feed>";

	private String expectedEntry1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title></entry>";

	private String badEntry1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\" id=\"notCool\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-02T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title></entry>";

	private String basicFeed1 = "<feed><title>Example</title><id>abc.123.xyz</id><updated>2020-12-13T18:30:02Z</updated><author><name>someone</name></author></feed>";

	private String basicFeed2 = "<feed xmlns:sort=\"http://www.colorfulsoftware.com/projects/atomsphere/extension/sort/1.0\"><sort:asc type=\"title\" /><title>Example</title><id>abc.123.xyz</id><updated>2020-12-13T18:30:02Z</updated><author><name>someone</name></author>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>"
			+ "</entry>"
			+ " <entry>"
			+ "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry></feed>";

	private String basicFeed3 = "<feed xmlns:sort=\"http://www.colorfulsoftware.com/projects/atomsphere/extension/sort/1.0\"><sort:asc type=\"summary\" /><title>Example</title><id>abc.123.xyz</id><updated>2020-12-13T18:30:02Z</updated><author><name>someone</name></author>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>"
			+ "</entry>"
			+ " <entry>"
			+ "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry></feed>";

	private String basicFeed4 = "<feed xmlns:sort=\"http://www.colorfulsoftware.com/projects/atomsphere/extension/sort/1.0\"><sort:desc type=\"title\" /><title>Example</title><id>abc.123.xyz</id><updated>2020-12-13T18:30:02Z</updated><author><name>someone</name></author>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>"
			+ "</entry>"
			+ " <entry>"
			+ "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry></feed>";

	private String basicFeed5 = "<feed xmlns:sort=\"http://www.colorfulsoftware.com/projects/atomsphere/extension/sort/1.0\"><sort:desc type=\"summary\" /><title>Example</title><id>abc.123.xyz</id><updated>2020-12-13T18:30:02Z</updated><author><name>someone</name></author>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title><summary>Some text.</summary>"
			+ "</entry>"
			+ " <entry>"
			+ "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry></feed>";

	private String basicFeed6 = "<feed xmlns:sort=\"http://www.colorfulsoftware.com/projects/atomsphere/extension/sort/1.0\"><sort:asc type=\"id\" /><title>Example</title><id>abc.123.xyz</id><updated>2020-12-13T18:30:02Z</updated><author><name>someone</name></author>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>125</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>"
			+ "</entry>"
			+ " <entry>"
			+ "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>123</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry></feed>";

	private String expectedFeed1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ " <title>Example Feed</title>"
			+ " <subtitle>A subtitle.</subtitle>"
			+ " <link href=\"http://example.org/feed/\" rel=\"self\"/>"
			+ " <link href=\"http://example.org/\"/>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>" + "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private String expectedFeed2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/fakeNamespace\">"
			+ " <title>Example Feed</title>"
			+ " <subtitle>A subtitle.</subtitle>"
			+ " <link href=\"http://example.org/feed/\" rel=\"self\"/>"
			+ " <link href=\"http://example.org/\"/>"
			+ " <local:test xmlns=\"http://purl.org/dc/elements/1.1/fakeNamespace\">things</local:test>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>" + "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private String expectedFeed3 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/fakeNamespace\">"
			+ " <title type=\"xhtml\"><div xmlns=\"http://www.w3.org/1999/xhtml\">/mean> Example Feed</div></title>"
			+ " <subtitle>A subtitle.</subtitle>"
			+ " <link href=\"http://example.org/feed/\" rel=\"self\"/>"
			+ " <link href=\"http://example.org/\"/>"
			+ " <local:test xmlns=\"http://purl.org/dc/elements/1.1/fakeNamespace\">things</local:test>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>" + "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private String expectedEntryExt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<author><name>anyone</name></author>"
			+ "<title>test entry 1</title>"
			+ "<link href=\"http://www.somewhere.com\" rel=\"alternate\" />"
			+ "<rights type=\"xhtml\"><div xmlns=\"http://www.w3.org/1999/xhtml\">A marked up <br /> rights.This is <span style=\"color:blue;\">blue text :). <hr id=\"unique\" class=\"phat\" /> <a href=\"http://maps.google.com?q=something&amp;b=somethingElse\">a fake map link</a></span>. </div></rights>"
			+ "<local:element xmlns:local=\"http://purl.org/dc/elements/1.1/fakeNamespace\">something that is an extension <local:embedded with=\"attribute\" /> Is this ok?</local:element>"
			+ "</entry>";

	private String badFeed1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/fakeNamespace\">"
			+ " <title>Example Feed</title>"
			+ " <subtitle type=\"xhtml\"><div>A marked up <br /> subtitle.</div></subtitle>"
			+ " <link href=\"http://example.org/feed/\" rel=\"self\"/>"
			+ " <link href=\"http://example.org/\"/>" + " <local:></local:>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>" + "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private String badFeed11 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/fakeNamespace\">"
			+ " <title>Example Feed</title>"
			+ " <subtitle type=\"xhtml\"><div>A marked up <br /> subtitle.</div></subtitle>"
			+ " <link href=\"http://example.org/feed/\" rel=\"self\"/>"
			+ " <link href=\"http://example.org/\"/>" + " <:local></:local>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>" + "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private String badFeed2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/fakeNamespace\">"
			+ " <title>Example Feed</title>"
			+ " <subtitle>A subtitle.</subtitle>"
			+ " <link href=\"http://example.org/feed/\" rel=\"self\"/>"
			+ " <link href=\"http://example.org/\"/>" + " <></>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>" + "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private String badFeed3 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/fakeNamespace\">"
			+ "<generator uri=\"http://www.colorfulsoftware.com/projects/atomsphere\" version=\"1.0.20\" nono=\"nono\">Atomsphere</generator>"
			+ " <title>Example Feed</title>"
			+ " <subtitle>A subtitle.</subtitle>"
			+ " <link href=\"http://example.org/feed/\" rel=\"self\"/>"
			+ " <link href=\"http://example.org/\"/>"
			+ " <local:test xmlns=\"http://purl.org/dc/elements/1.1/fakeNamespace\">things</local:test>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>" + "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private String badFeed4 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/fakeNamespace\">"
			+ " <title>Example Feed</title>"
			+ " <subtitle>A subtitle.</subtitle>"
			+ " <link rel=\"self\"/>"
			+ " <link href=\"http://example.org/\"/>"
			+ " <local:test xmlns=\"http://purl.org/dc/elements/1.1/fakeNamespace\">things</local:test>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>" + "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private String badFeed5 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/fakeNamespace\">"
			+ " <title>Example Feed</title>"
			+ " <subtitle>A subtitle.</subtitle>"
			+ " <link />"
			+ " <link href=\"http://example.org/\"/>"
			+ " <local:test xmlns=\"http://purl.org/dc/elements/1.1/fakeNamespace\">things</local:test>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>" + "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private String badFeed6 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/fakeNamespace\">"
			+ " <title>Example Feed</title>"
			+ " <subtitle type=\"xhtml\"><div>A marked up <br /> subtitle.</div></subtitle>"
			+ " <link href=\"http://example.org/feed/\" rel=\"self\"/>"
			+ " <link href=\"http://example.org/\"/>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>"
			+ " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry xml:lang=\"en-US\">"
			+ "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			// add this to the feed after creation.
			// + " <unbound:ext>some text</unbound:ext>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private Entry entry1, entry2, entry3;

	// found these examples here:
	// http://www.xml.com/pub/a/2005/12/07/handling-atom-text-and-content-constructs.html
	// that should apply to text constructs:
	// title, subtitle, summary, rights.
	private String title1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title>One bold foot forward</title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	private String title2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title type=\"text\">One bold foot forward</title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	// bad example here. the code actually unencodes the text.
	private String title3 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title>One &lt;strong&gt;bold&lt;/strong&gt; foot forward</title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	private String title4 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title type=\"html\">One &lt;strong&gt;bold&lt;/strong&gt; foot forward</title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	private String badCat1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title>One bold foot forward</title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "<category baseball=\"no\">so what</category>" + "</feed>";

	private String badCat2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title>One bold foot forward</title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "<category>so what</category>" + "</feed>";
	/*
	 * currently of the 3 implementations tested: sjsxp stax woodstox None of
	 * them are able to detect CDATA sections. so the markup display ends up
	 * being escaped in the output without the cdata section.
	 */
	private String title5 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title type=\"html\"><![CDATA[One <strong>bold</strong> foot forward]]></title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	private String title6 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title type=\"xhtml\"><div xmlns=\"http://www.w3.org/1999/xhtml\">One <strong>bold</strong> foot forward<title>can you see me</title></div></title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	private String title7 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\"  xmlns:xh=\"http://www.w3.org/1999/xhtml\">"
			+ "<title type=\"xhtml\"><xh:div>One <xh:strong>bold</xh:strong> foot forward </xh:div></title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	private String brokeTitle1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\"  xmlns:xh=\"http://www.w3.org/1999/xhtml\">"
			+ "<title type=\"xhtml\" fakeAttr=\"bunk\"><xh:div>One <xh:strong>bold</xh:strong> foot forward </xh:div></title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";
	// /

	// mising id
	private String brokenEntry1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			// + "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-03T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>" + "</entry>";

	// missing title
	private String brokenEntry2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-04T00:00:00.00-06:00</updated></entry>";
	// + "<title>test entry 1</title>" + "</entry>";

	// missing updated
	private String brokenEntry3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			// + "<updated>2008-01-05T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>" + "</entry>";

	// missing summary attribute for empty content element
	private String brokenEntry4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-06T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>"
			+ "<content src=\"missingSummaryAttr\" />" + "</entry>";

	// bad content content type
	private String brokenEntry5 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-07T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>"
			+ "<content type=\"pdf\">this is no good</content>" + "</entry>";

	// unsupported attribute
	private String brokenEntry6 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\" fakeAttribute=\"noGood\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-08T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>" + "</entry>";

	/**
	 * @throws Exception
	 *             if there is an error creating the test data.
	 */
	@Before
	public void setUp() throws Exception {
		try {
			feedDoc = new FeedDoc();
			Id id = feedDoc.buildId(null,
					"http://www.colorfulsoftware.com/atom.xml");

			Updated updated = feedDoc.buildUpdated(null, Calendar.getInstance()
					.getTime().toString());

			Title title = feedDoc.buildTitle("test feed", null);

			Generator generator = feedDoc.getLibVersion();

			List<Author> authors = new LinkedList<Author>();
			authors.add(feedDoc.buildAuthor(feedDoc.buildName("Bill Brown"),
					null, null, null, null));

			try {
				feedDoc.buildAuthor(null, null, null, null, null);
				fail("should not get here;");
			} catch (Exception e) {
				assertTrue(e instanceof AtomSpecException);
				assertEquals(e.getMessage(),
						"Person constructs MUST contain exactly one \"atom:name\" element.");
			}

			try {
				List<Attribute> attrs = new LinkedList<Attribute>();
				attrs.add(feedDoc.buildAttribute("goofy", "attrValue"));
				feedDoc.buildAuthor(new Name("You"), null, null, attrs, null);
				fail("should not get here;");
			} catch (Exception e) {
				assertTrue(e instanceof AtomSpecException);
				assertEquals(e.getMessage(),
						"Unsupported attribute goofy for this Atom Person Construct.");
			}

			feed1 = feedDoc.buildFeed(id, title, updated, null, authors, null,
					null, null, null, null, generator, null, null, null, null);

			entry1 = feedDoc.buildEntry(feedDoc.buildId(null,
					"http://www.colorfulsoftware.com/projects/atomsphere/"),
					feedDoc.buildTitle("test entry 1", null), feedDoc
							.buildUpdated(null, theDate.getTime().toString()),
					null, null, null, null, null, null, null, null, null, null,
					null);

			entry2 = feedDoc.buildEntry(feedDoc.buildId(null,
					"http://www.colorfulsoftware.com/projects/atomsphere/"),
					feedDoc.buildTitle("test entry 2", null), feedDoc
							.buildUpdated(null, theDate.getTime().toString()),
					null, null, null, null, null, null, null, null, null, null,
					null);

			entry3 = feedDoc.buildEntry(feedDoc.buildId(null,
					"http://www.colorfulsoftware.com/projects/atomsphere/"),
					feedDoc.buildTitle("test entry 3", null), feedDoc
							.buildUpdated(null, theDate.getTime().toString()),
					null, null, null, null, null, null, null, null, null, null,
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// add the indenting stream writer jar to the classpath
		// http://forums.sun.com/thread.jspa?threadID=300557&start=0&tstart=0
		URLClassLoader sysloader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL",
					java.net.URL.class);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { new java.net.URL(
					"http://ftpna2.bea.com/pub/downloads/jsr173.jar") });
			method
					.invoke(
							sysloader,
							new Object[] { new java.net.URL(
									"http://repo2.maven.org/maven2/net/java/dev/stax-utils/stax-utils/20060502/stax-utils-20060502.jar") });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException(
					"Error, could not add URL to system classloader");
		}// end try catch
	}

	/**
	 * @throws Exception
	 *             if there is an error cleaning up the test data.
	 */
	@After
	public void tearDown() throws Exception {
		new File("target/out.xml").deleteOnExit();
		new File("target/out2.xml").deleteOnExit();
	}

	/**
	 * test the output stream functionality.
	 */
	@Test
	public void testWriteFeedDocOutputStreamFeedStringString() {
		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://deals.ebay.com/feeds/rss"));
			feedDoc.writeFeedDoc(new FileOutputStream("target/out1.xml"),
					feed1, feedDoc.getEncoding(), feedDoc.getXmlVersion());
			Feed feed2 = feedDoc.readFeedToBean(new File("target/out1.xml"));
			assertNotNull(feed2);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
		}

		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
				"http://deals.ebay.com/feeds/rss"));
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream("target/out2.xml"), "UTF-8");
			feedDoc.writeFeedDoc(writer, feed1, "UTF-8", feedDoc
					.getXmlVersion());
			Feed feed2 = feedDoc.readFeedToBean(new File("target/out2.xml"));
			assertNotNull(feed2);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
		}

		/*
		 * uncomment stax-utils dependency in the root pom.xml to see example in
		 * action try { feed1 = feedDoc.readFeedToBean(new java.net.URL(
		 * "http://deals.ebay.com/feeds/rss"));
		 * feedDoc.writeFeedDoc(new IndentingXMLStreamWriter(XMLOutputFactory
		 * .newInstance().createXMLStreamWriter( new
		 * FileOutputStream("target/out3.xml"), "UTF-8")), feed1,
		 * feedDoc.encoding, feedDoc.xml_version); Feed feed2 =
		 * feedDoc.readFeedToBean(new File("target/out3.xml"));
		 * assertNotNull(feed2); } catch (Exception e) { e.printStackTrace();
		 * assertTrue(e instanceof XMLStreamException); }
		 */
	}

	/**
	 * test the output stream functionality.
	 */
	@Test
	public void testWriteEntryDocOutputStreamEntryStringString() {
		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://earthbeats.net/http://deals.ebay.com/feeds/rss"));
			feedDoc.writeEntryDoc(new FileOutputStream("target/out5.xml"),
					feed1.getEntries().get(0), feedDoc.getEncoding(), feedDoc
							.getXmlVersion());
			Entry entry1 = feedDoc.readEntryToBean(new File("target/out5.xml"));
			assertNotNull(entry1);
		} catch (Exception e) {
			e.printStackTrace();
			fail("should not get here.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://deals.ebay.com/feeds/rss"));
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream("target/out2.xml"), "UTF-8");
			feedDoc.writeEntryDoc(writer, feed1.getEntries().get(0), "UTF-8",
					feedDoc.getXmlVersion());
			Entry entry1 = feedDoc.readEntryToBean(new File("target/out2.xml"));
			assertNotNull(entry1);
		} catch (Exception e) {
			e.printStackTrace();
			fail("should not get here.");
		}

		// test a bad output.
		try {
			feedDoc.writeEntryDoc(new FileWriter("target/file.bunk"), null,
					null, null);
			fail("this should not happen.");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"The atom entry object cannot be null.");
		}

		// test a bad output.
		try {
			feedDoc.writeFeedDoc(new FileWriter("target/file.bunk"), null,
					null, null);
			fail("this should not happen.");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(), "The atom feed object cannot be null.");
		}
	}

	/**
	 * test the stream writer functionality.
	 */
	@Test
	public void testWriteFeedDocXMLStreamWriterFeedStringString() {
		try {
			// pretty print version.
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://deals.ebay.com/feeds/rss"));
			feedDoc.writeFeedDoc(XMLOutputFactory.newInstance()
					.createXMLStreamWriter(
							new FileOutputStream("target/out2.xml"),
							feedDoc.getEncoding()), feed1, feedDoc
					.getEncoding(), feedDoc.getXmlVersion());
			Feed feed2 = feedDoc.readFeedToBean(new File("target/out2.xml"));
			assertNotNull(feed2);
		} catch (Exception e) {
			e.printStackTrace();
			fail("should not get here.");
		}
	}

	/**
	 * test the feed doc constructors.
	 */
	@Test
	public void testFeedDocTest() {
		try {
			FeedDoc feedDoc2 = null;
			List<FeedDoc.ProcessingInstruction> insts = new LinkedList<FeedDoc.ProcessingInstruction>();
			insts
					.add(new FeedDoc().new ProcessingInstruction(
							"xml-stylesheet",
							"href=\"http://www.blogger.com/styles/atom.css\" type=\"text/css\""));
			feedDoc2 = new FeedDoc(insts);
			assertNotNull(feedDoc2);
			String output = feedDoc2.readFeedToString(feedDoc2
					.readFeedToBean(new java.net.URL(
							"http://deals.ebay.com/feeds/rss")), null);
			assertTrue(output
					.indexOf("<?xml-stylesheet href=\"http://www.blogger.com/styles/atom.css\" type=\"text/css\"?>") != -1);
		} catch (Exception e) {
			e.printStackTrace();
			fail("should not get here.");
		}
	}

	/**
	 * test the string reading functionality.
	 */
	@Test
	public void testReadFeedToStringFeedString() {
		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://deals.ebay.com/feeds/rss"));
			String feedStr = feedDoc.readFeedToString(feed1,
					"com.sun.xml.txw2.output.IndentingXMLStreamWriter");
			assertNotNull(feedStr);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
		}

		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://deals.ebay.com/feeds/rss"));
			String feedStr = feedDoc.readFeedToString(feed1,
					"javanet.staxutils.IndentingXMLStreamWriter");
			assertNotNull(feedStr);
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
		}

		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://deals.ebay.com/feeds/rss"));
			String feedStr = feedDoc.readFeedToString(feed1, "bunk");
			assertNotNull(feedStr);
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
		}
	}

	/**
	 * test the string reading functionality.
	 */
	@Test
	public void testReadFeedToStringFeed() {

		// this feed has processing instructions.
		try {
			feed1 = feedDoc
					.readFeedToBean(new URL(
							"http://omsa-opportunities.blogspot.com/feeds/posts/default"));
			assertNotNull(feed1);
			feedDoc.writeFeedDoc(XMLOutputFactory.newInstance()
					.createXMLStreamWriter(
							new FileOutputStream("target/procInst.xml")),
					feed1, null, null);
			// fail();
			feed1 = feedDoc.readFeedToBean(new File("target/procInst.xml"));
			assertNotNull(feed1);
		} catch (Exception e) {
			e.printStackTrace();
			fail("this shouldn't happen.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(expectedFeed1);
			assertNotNull(feed1.toString());
			feed1 = feedDoc.readFeedToBean(feed1.toString());
			assertNotNull(feed1);
			assertNotNull(feed1.getId());
			assertNotNull(feed1.getTitle());
			Updated updated = feed1.getUpdated();
			assertNotNull(updated);
			assertNotNull(updated.getAttribute("xml:lang"));
			assertNull(updated.getAttribute("bunky"));

		} catch (Exception e) {
			e.printStackTrace();
			fail("should not get here.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(basicFeed1);
			assertNull(feed1.getAttributes());
			assertNull(feed1.getAttribute("xmlns"));
			assertNull(feed1.getAttribute("xml:lang"));
			XMLStreamWriter writer = XMLOutputFactory.newInstance()
					.createXMLStreamWriter(
							new FileOutputStream("target/basicFeed1.xml"));
			feedDoc.writeFeedDoc(writer, feed1, null, null);

			feed1 = feedDoc.readFeedToBean(new File("target/basicFeed1.xml"));
			assertNotNull(feed1.getAttributes());
			assertNotNull(feed1.getAttribute("xmlns"));
			assertNotNull(feed1.getAttribute("xml:lang"));

			// do some sorting
			feed1 = feedDoc.readFeedToBean(basicFeed2);
			writer = XMLOutputFactory.newInstance().createXMLStreamWriter(
					new FileOutputStream("target/basicFeed2.xml"));
			feedDoc.writeFeedDoc(writer, feed1, null, null);

			try {
				feed1 = feedDoc.readFeedToBean(basicFeed3);
				writer = XMLOutputFactory.newInstance().createXMLStreamWriter(
						new FileOutputStream("target/basicFeed3.xml"));
				feedDoc.writeFeedDoc(writer, feed1, null, null);
				fail("should not get here.");
			} catch (AtomSpecException e) {
				assertEquals(
						e.getMessage(),
						"The feed entries cannot be sorted by <summary> because not all of them have one.");
			}

			feed1 = feedDoc.readFeedToBean(basicFeed4);
			writer = XMLOutputFactory.newInstance().createXMLStreamWriter(
					new FileOutputStream("target/basicFeed4.xml"));
			feedDoc.writeFeedDoc(writer, feed1, null, null);

			feed1 = feedDoc.readFeedToBean(basicFeed5);
			writer = XMLOutputFactory.newInstance().createXMLStreamWriter(
					new FileOutputStream("target/basicFeed5.xml"));
			feedDoc.writeFeedDoc(writer, feed1, null, null);

		} catch (Exception e) {
			e.printStackTrace();
			fail("should not get here.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(expectedFeed2);
			assertNotNull(feed1.getExtension("local:test")
					.getAttribute("xmlns"));
			assertNull(feed1.getExtension("local:test").getAttribute("bunky"));

		} catch (Exception e) {
			e.printStackTrace();
			fail("should not get here.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(expectedFeed3);
			assertNotNull(feed1);
			assertEquals(feed1.getTitle().getText(), "/mean> Example Feed");
			BufferedWriter fout = new BufferedWriter(new FileWriter(
					"target/expectedFeed3.xml"));
			fout.write(feed1.toString());
			fout.flush();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail("should not get here.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(badFeed1);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Extension element '' is missing a namespace prefix or namespace declaration.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(badFeed11);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"Extension element ':local' is missing a namespace prefix or namespace declaration.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(badFeed2);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof javax.xml.stream.XMLStreamException);
		}

		try {// bad generator
			feed1 = feedDoc.readFeedToBean(badFeed3);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Unsupported attribute nono in the atom:generator element.");
		}

		try {// bad link no href
			feed1 = feedDoc.readFeedToBean(badFeed4);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"atom:link elements MUST have an href attribute, whose value MUST be a IRI reference.");
		}

		try {// bad link empty
			feed1 = feedDoc.readFeedToBean(badFeed5);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"atom:link elements MUST have an href attribute, whose value MUST be a IRI reference.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(badFeed6);
			Entry ent = feed1.getEntry("Atom-Powered Robots Run Amok");
			Extension ext = feedDoc.buildExtension("unbound:ele", null, null);
			List<Extension> extns = new LinkedList<Extension>();
			extns.add(ext);
			ent = feedDoc.buildEntry(ent.getId(), ent.getTitle(), ent
					.getUpdated(), ent.getRights(), ent.getContent(), ent
					.getAuthors(), ent.getCategories(), ent.getContributors(),
					ent.getLinks(), ent.getAttributes(), extns, ent
							.getPublished(), ent.getSummary(), ent.getSource());
			List<Entry> entries = feed1.getEntries();
			for (int i = 0; i < entries.size(); i++) {
				if (entries.get(i).getTitle().getText().equals(
						"Atom-Powered Robots Run Amok")) {
					entries.remove(i);
					break;
				}
			}
			entries.add(ent);
			feedDoc.buildFeed(feed1.getId(), feed1.getTitle(), feed1
					.getUpdated(), feed1.getRights(), feed1.getAuthors(), feed1
					.getCategories(), feed1.getContributors(),
					feed1.getLinks(), feed1.getAttributes(), feed1
							.getExtensions(), feed1.getGenerator(), feed1
							.getSubtitle(), feed1.getIcon(), feed1.getLogo(),
					entries);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"the following extension prefix(es) ( unbound ) are not bound to a namespace declaration. See http://www.w3.org/TR/1999/REC-xml-names-19990114/#ns-decl.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(badFeed6);
			Entry ent = feed1.getEntry("Atom-Powered Robots Run Amok");
			feedDoc.writeEntryDoc(new FileOutputStream(new File(
					"target/works.xml")), ent, null, null);
			Extension ext = feedDoc.buildExtension("unbound:ele", null, null);
			List<Extension> extns = new LinkedList<Extension>();
			extns.add(ext);
			ent = feedDoc.buildEntry(ent.getId(), ent.getTitle(), ent
					.getUpdated(), ent.getRights(), ent.getContent(), ent
					.getAuthors(), ent.getCategories(), ent.getContributors(),
					ent.getLinks(), ent.getAttributes(), extns, ent
							.getPublished(), ent.getSummary(), ent.getSource());
			feedDoc.writeEntryDoc(new FileOutputStream(new File(
					"target/never.xml")), ent, null, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"the following extension prefix(es) ( unbound ) are not bound to a namespace declaration. See http://www.w3.org/TR/1999/REC-xml-names-19990114/#ns-decl.");
		}

		// test the seven title variants.
		try {

			feed1 = feedDoc.readFeedToBean(title1);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(), "One bold foot forward");
			assertNotNull(feedDoc.readFeedToString(feed1, ""));
			assertNotNull(feed1.toString());

			feed1 = feedDoc.readFeedToBean(title2);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(), "One bold foot forward");
			assertNotNull(feedDoc.readFeedToString(feed1, ""));
			assertNotNull(feed1.toString());

			feed1 = feedDoc.readFeedToBean(title3);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(),
					"One <strong>bold</strong> foot forward");
			assertNotNull(feedDoc.readFeedToString(feed1, ""));
			assertNotNull(feed1.toString());

			feed1 = feedDoc.readFeedToBean(title4);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(),
					"One &lt;strong&gt;bold&lt;/strong&gt; foot forward");
			assertNotNull(feedDoc.readFeedToString(feed1, ""));
			assertNotNull(feed1.toString());

			/*
			 * currently of the 3 implementations tested: sjsxp stax woodstox
			 * None of them are able to detect CDATA sections.
			 */
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = inputFactory
					.createXMLStreamReader(new java.io.StringReader(title5));
			assertFalse(checkForCDATA(reader));

			feed1 = feedDoc.readFeedToBean(title5);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(),
					"One &lt;strong&gt;bold&lt;/strong&gt; foot forward");
			assertNotNull(feedDoc.readFeedToString(feed1, ""));
			assertNotNull(feed1.toString());

			feed1 = feedDoc.readFeedToBean(title6);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(),
					"One <strong>bold</strong> foot forward<title>can you see me</title>");
			assertNotNull(feedDoc.readFeedToString(feed1, ""));
			assertNotNull(feed1.toString());

			feed1 = feedDoc.readFeedToBean(title7);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(),
					"One <xh:strong>bold</xh:strong> foot forward ");
			assertNotNull(feed1.getTitle().getAttribute("type"));
			assertNull(feed1.getTitle().getAttribute("bunk"));
			assertNotNull(feedDoc.readFeedToString(feed1, ""));
			assertNotNull(feed1.toString());

			try {
				feed1 = feedDoc.readFeedToBean(brokeTitle1);
				fail("should not get here.");
			} catch (Exception e) {
				assertTrue(e instanceof AtomSpecException);
				assertEquals(e.getMessage(),
						"Unsupported attribute fakeAttr for this Atom Text Construct.");
			}

			try {
				feed1 = feedDoc.readFeedToBean(badCat1);
				fail("should not get here.");
			} catch (Exception e) {
				assertTrue(e instanceof AtomSpecException);
				assertEquals(e.getMessage(),
						"Unsupported attribute baseball in the atom:category element.");
			}

			try {
				feed1 = feedDoc.readFeedToBean(badCat2);
				fail("should not get here.");
			} catch (Exception e) {
				assertTrue(e instanceof AtomSpecException);
				assertEquals(e.getMessage(),
						"Category elements MUST have a \"term\" attribute.");
			}

			String feedStr = feedDoc.readFeedToString(feedDoc
					.readFeedToBean(new URL(
							"http://deals.ebay.com/feeds/rss")),
					"javanet.staxutils.IndentingXMLStreamWriter");
			assertNotNull(feedStr);
			BufferedWriter fout = new BufferedWriter(new FileWriter(
					"target/indentedDrops.xml"));
			fout.write(feedStr);
			fout.flush();
			fout.close();

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
		}

		try {
			feedDoc.readFeedToString(null, null);
			fail("this should not happen.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(), "The atom feed object cannot be null.");
		}

		// test extension namespaces
		try {
			feed1 = feedDoc.readFeedToBean(basicFeed4);
			List<Author> auths = feed1.getAuthors();
			Author author = auths.remove(0);
			List<Extension> extns = new LinkedList<Extension>();
			extns.add(feedDoc.buildExtension("whats:up", null,
					"nothing special"));
			auths.add(feedDoc.buildAuthor(author.getName(), author.getUri(),
					author.getEmail(), author.getAttributes(), extns));
			feedDoc.buildFeed(feed1.getId(), feed1.getTitle(), feed1
					.getUpdated(), feed1.getRights(), auths, feed1
					.getCategories(), feed1.getContributors(),
					feed1.getLinks(), feed1.getAttributes(), feed1
							.getExtensions(), feed1.getGenerator(), feed1
							.getSubtitle(), feed1.getIcon(), feed1.getLogo(),
					feed1.getEntries());
			fail("this should not happen.");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"the following extension prefix(es) ( whats ) are not bound to a namespace declaration. See http://www.w3.org/TR/1999/REC-xml-names-19990114/#ns-decl.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(basicFeed4);
			List<Author> auths = feed1.getAuthors();
			Author author = auths.remove(0);
			List<Extension> extns = new LinkedList<Extension>();
			extns.add(feedDoc.buildExtension("whats:up", null,
					"nothing special"));
			List<Attribute> attrs = feed1.getAttributes();
			attrs.add(feedDoc.buildAttribute("xmlns:whats",
					"http://www.yyy.zzz"));
			auths.add(feedDoc.buildAuthor(author.getName(), author.getUri(),
					author.getEmail(), author.getAttributes(), extns));
			feed1 = feedDoc.buildFeed(feed1.getId(), feed1.getTitle(), feed1
					.getUpdated(), feed1.getRights(), auths, feed1
					.getCategories(), feed1.getContributors(),
					feed1.getLinks(), attrs, feed1.getExtensions(), feed1
							.getGenerator(), feed1.getSubtitle(), feed1
							.getIcon(), feed1.getLogo(), null);
			assertNotNull(feed1);
			assertNotNull(feed1.getAuthor("someone").getExtension("whats:up"));
			assertNull(feed1.getEntry("anything"));
		} catch (Exception e) {
			fail("this should not happen.");
		}
	}

	// debugging to see if we hit any CDATA sections.
	boolean checkForCDATA(XMLStreamReader reader) throws Exception {
		while (reader.hasNext()) {
			int next = reader.next();
			switch (next) {
			case XMLStreamConstants.START_DOCUMENT:
				break;
			case XMLStreamConstants.START_ELEMENT:
				break;
			case XMLStreamConstants.END_ELEMENT:
				break;
			case XMLStreamConstants.ATTRIBUTE:
				break;
			case XMLStreamConstants.CDATA:
				return true;
			case XMLStreamConstants.CHARACTERS:
				break;
			case XMLStreamConstants.COMMENT:
				break;
			case XMLStreamConstants.DTD:
				break;
			case XMLStreamConstants.END_DOCUMENT:
				break;
			case XMLStreamConstants.ENTITY_DECLARATION:
				break;
			case XMLStreamConstants.ENTITY_REFERENCE:
				break;
			case XMLStreamConstants.NAMESPACE:
				break;
			case XMLStreamConstants.NOTATION_DECLARATION:
				break;
			case XMLStreamConstants.PROCESSING_INSTRUCTION:
				break;
			case XMLStreamConstants.SPACE:
				break;
			}
		}
		return false;
	}

	/**
	 * test the string reading functionality.
	 */
	@Test
	public void testReadEntryToString() {
		try {
			String entryStr = feedDoc.readEntryToString(
					feedDoc.readEntryToBean(expectedEntry1), null).toString();
			assertTrue(entryStr != null);
			assertEquals(entryStr, expectedEntry1);
		} catch (Exception e) {
			e.printStackTrace();
			fail("this should not happen.");
		}

		try {
			String entryStr = feedDoc.readEntryToString(entry1,
					"javanet.staxutils.IndentingXMLStreamWriter");
			assertTrue(entryStr != null);
		} catch (Exception e) {
			fail("this should not happen.");
		}

		// test a bad writer.
		try {
			String entryStr = feedDoc.readEntryToString(entry1,
					"com.fake.BunkWriter");
			assertTrue(entryStr != null);
		} catch (Exception e) {
			fail("this should not happen.");
		}

		// test a bad reader.
		try {
			feedDoc.readEntryToString(null, null);
			fail("this should not happen.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"The atom entry object cannot be null.");
		}
	}

	/**
	 * test the string reading functionality.
	 */
	@Test
	public void testReadEntryToBeanString() {
		Entry entry;
		try {
			entry = feedDoc.readEntryToBean(brokenEntry1);
			assertTrue(entry == null);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"atom:entry elements MUST contain exactly one atom:id element.");
		}

		try {
			entry = feedDoc.readEntryToBean(brokenEntry2);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"atom:entry elements MUST contain exactly one atom:title element.");
		}

		try {
			entry = feedDoc.readEntryToBean(brokenEntry3);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"atom:entry elements MUST contain exactly one atom:updated element.");
		}

		try {
			entry = feedDoc.readEntryToBean(brokenEntry4);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"atom:entry elements MUST contain an atom:summary element if the atom:entry contains an atom:content that has a \"src\" attribute (and is thus empty).");
		}

		try {
			entry = feedDoc.readEntryToBean(brokenEntry5);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Unsupported attribute type for this Atom Text Construct.");
		}

		try {
			entry = feedDoc.readEntryToBean(brokenEntry6);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Unsupported attribute fakeAttribute for this element.");
		}

		try {
			entry = feedDoc.readEntryToBean(expectedEntry1);
			assertNotNull(entry);
		} catch (Exception e) {
			fail("should not get here;");
		}

		try {
			entry = feedDoc.readEntryToBean(badEntry1);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Unsupported attribute id for this element.");
		}

		try {
			entry = feedDoc.readEntryToBean(new File(
					"src/test/resources/expectedEntry1.xml"));
			assertNotNull(entry);
		} catch (Exception e) {
			fail("should not get here;");
		}

		try {
			entry = feedDoc.readEntryToBean(new URL(
					"http://deals.ebay.com/feeds/rss"));
			assertNotNull(entry);
		} catch (Exception e) {
			e.printStackTrace();
			fail("should not get here;");
		}
	}

	/**
	 * tests building an attribute
	 */
	@Test
	public void testBuildAttribute() {
		try {
			feedDoc.buildAttribute(null, null);
			fail("should not get here.");
		} catch (AtomSpecException r) {
			assertEquals(r.getMessage(), "Attribute names SHOULD NOT be blank.");
		}

		try {
			feedDoc.buildAttribute("", null);
			fail("should not get here.");
		} catch (AtomSpecException r) {
			assertEquals(r.getMessage(), "Attribute names SHOULD NOT be blank.");
		}

		try {
			Attribute attr = feedDoc.buildAttribute("yep", null);
			assertNotNull(attr);
		} catch (AtomSpecException r) {
			fail("should not get here.");
		}

		try {
			Attribute attr = feedDoc.buildAttribute("yep", "");
			assertNotNull(attr);
			Attribute attr2 = feedDoc.buildAttribute("yep", "");
			assertTrue(attr.equals(attr2));
			attr2 = feedDoc.buildAttribute("yep", null);
			assertTrue(attr.equals(attr2));
		} catch (AtomSpecException r) {
			fail("should not get here.");
		}
	}

	/**
	 * tests building an author
	 */
	@Test
	public void testBuildAuthor() {

		try {
			feedDoc.buildAuthor(null, feedDoc.buildURI(null), feedDoc
					.buildEmail(null), null, null);
			fail("should not get here.");
		} catch (AtomSpecException r) {
			assertEquals(r.getMessage(),
					"Person constructs MUST contain exactly one \"atom:name\" element.");
		}

		try {
			feedDoc.buildAuthor(feedDoc.buildName(null),
					feedDoc.buildURI(null), feedDoc.buildEmail(null), null,
					null);
			fail("should not get here.");
		} catch (AtomSpecException r) {
			assertEquals(r.getMessage(), "The person name SHOULD NOT be blank.");
		}

		try {
			feedDoc.buildAuthor(feedDoc.buildName(""), feedDoc.buildURI(null),
					feedDoc.buildEmail(null), null, null);
			fail("should not get here.");
		} catch (AtomSpecException r) {
			assertEquals(r.getMessage(), "The person name SHOULD NOT be blank.");
		}

		try {
			Author author = feedDoc.buildAuthor(feedDoc.buildName("someone"),
					feedDoc.buildURI(null), feedDoc.buildEmail(null), null,
					null);
			assertNotNull(author);
		} catch (AtomSpecException r) {
			fail("should not get here.");
		}
	}

	/**
	 * test the feed building dates.
	 */
	@Test
	public void testBuildDate() {
		try {
			feedDoc.buildPublished(null, null);
			fail("should not get here.");
		} catch (AtomSpecException e) {
			assertEquals(e.getMessage(),
					"AtomDateConstruct Dates SHOULD NOT be blank.");
		}

		try {
			Attribute err = feedDoc.buildAttribute("href", "bunk");
			List<Attribute> attrs = new LinkedList<Attribute>();
			attrs.add(err);
			feedDoc.buildPublished(attrs, null);
			fail("should not get here.");
		} catch (AtomSpecException e) {
			assertEquals(e.getMessage(),
					"Unsupported attribute href for this Atom Date Construct.");
		}

		try {
			// roll the date back to a different timezone
			TimeZone.setDefault(TimeZone.getTimeZone("Etc/GMT-10"));
			Published pub = feedDoc.buildPublished(null, Calendar.getInstance()
					.getTime().toString());
			assertTrue(pub.getText() != null);
			TimeZone.setDefault(null);
		} catch (AtomSpecException e) {
			fail("should not get here.");
		}

		try {
			// test bad date string
			Published pub = feedDoc.buildPublished(null,
					"2009-10-15T11:11:30.52Z");
			assertTrue(pub.getText() != null);
			assertTrue(pub.getText().equals("2009-10-15T11:11:30.52Z"));
		} catch (AtomSpecException e) {
			fail("should not get here.");
		}

		try {
			// test bad date string
			feedDoc.buildPublished(null, "abcdefg");
			fail("should not get here.");
		} catch (AtomSpecException e) {
			assertEquals(e.getMessage(),
					"error trying to create the date element with string: abcdefg");
		}

		// test adjusted date strings
		//
		try {
			// test bad date string
			Published pub = feedDoc.buildPublished(null,
					"2010-02-17T02:54:20+02:00");
			assertTrue(pub.getText() != null);
			assertTrue(pub.getText().equals("2010-02-17T02:54:20+02:00"));

			pub = feedDoc.buildPublished(null, "2010-02-17T02:54:20-04:00");
			assertTrue(pub.getText() != null);
			assertTrue(pub.getText().equals("2010-02-17T02:54:20-04:00"));

			pub = feedDoc.buildPublished(null, "2010-02-17T02:54:20+00:00");
			assertTrue(pub.getText() != null);
			assertTrue(pub.getText().equals("2010-02-17T02:54:20+00:00"));

		} catch (AtomSpecException e) {
			fail("should not get here.");
		}
	}

	/**
	 * test the feed building functionality.
	 */
	@Test
	public void testBuildFeed() {
		try {
			Generator generator = feedDoc.getLibVersion();

			Id id = feedDoc.buildId(null,
					"http://www.colorfulsoftware.com/atom.xml");

			Updated updated = feedDoc.buildUpdated(null, Calendar.getInstance()
					.getTime().toString());

			Title title = feedDoc.buildTitle("test feed", null);

			List<Contributor> contributors = new LinkedList<Contributor>();
			Contributor contributor = feedDoc.buildContributor(new Name(
					"Mad Dog"), null, feedDoc.buildEmail("info@maddog.net"),
					null, null);
			contributors.add(contributor);

			Rights rights = feedDoc.buildRights("GPL 1.0", null);

			Icon icon = feedDoc.buildIcon(null, "http://host/images/icon.png");

			Logo logo = feedDoc.buildLogo(null, "http://host/images/logo.png");

			List<Attribute> catAttrs = new LinkedList<Attribute>();
			catAttrs.add(feedDoc.buildAttribute("term", "music"));
			catAttrs.add(feedDoc.buildAttribute("scheme",
					"http://mtv.com/genere"));
			catAttrs.add(feedDoc.buildAttribute("label", "music"));
			List<Category> categories = new LinkedList<Category>();
			Category category = feedDoc.buildCategory(catAttrs, null);
			categories.add(category);

			List<Attribute> linkAttrs = new LinkedList<Attribute>();
			linkAttrs.add(feedDoc
					.buildAttribute("href", "http://www.yahoo.com"));
			linkAttrs.add(feedDoc.buildAttribute("rel", "self"));
			linkAttrs.add(feedDoc.buildAttribute("hreflang", "en-US"));
			List<Link> links = new LinkedList<Link>();
			Link link = feedDoc.buildLink(linkAttrs, null);
			links.add(link);

			Attribute extAttr = feedDoc.buildAttribute("xmlns:xhtml",
					"http://www.w3.org/1999/xhtml");
			List<Attribute> extAttrs = new LinkedList<Attribute>();
			extAttrs.add(extAttr);

			// the base feed attributes.
			List<Attribute> feedAttrs = new LinkedList<Attribute>();
			feedAttrs.add(feedDoc.getAtomBase());
			feedAttrs.add(feedDoc.getLangEn());
			feedAttrs.addAll(extAttrs);

			List<Extension> extensions = new LinkedList<Extension>();
			Extension extension = feedDoc.buildExtension("xhtml:div", null,
					"<span style='color:red;'>hello there</span>");
			extensions.add(extension);

			List<Author> authors = new LinkedList<Author>();
			authors.add(feedDoc.buildAuthor(feedDoc.buildName("Bill Brown"),
					null, null, null, null));
			Entry entry = feedDoc
					.buildEntry(
							feedDoc
									.buildId(null,
											"http://www.colorfulsoftware.com/atom.xml#entry1"),
							feedDoc.buildTitle("an example atom entry", null),
							feedDoc.buildUpdated(null, Calendar.getInstance()
									.getTime().toString()),
							null,
							feedDoc
									.buildContent(
											"Hello World.  Welcome to the atomsphere feed builder for atom 1.0 builds.  I hope it is useful for you.",
											null), authors, null, null, null,
							null, null, null, null, null);

			List<Entry> entries = new LinkedList<Entry>();
			entries.add(entry);

			Feed feed = feedDoc.buildFeed(id, title, updated, rights, authors,
					categories, contributors, links, feedAttrs, extensions,
					generator, null, icon, logo, entries);

			assertNotNull(feed);
			assertNotNull(feed.getAttribute("xml:lang"));
			assertNull(feed.getAttribute("bunk"));

			// read and write a full feed.
			feed = feedDoc.readFeedToBean(mega);
			assertNotNull(feedDoc.getLibVersion());
			BufferedWriter out = new BufferedWriter(new FileWriter(
					"target/mega.xml"));
			out.write(feed.toString());
			out.flush();
			out.close();
			feedDoc.writeFeedDoc(new FileOutputStream(new File(
					"target/mega2.xml")), feed, feedDoc.getEncoding(), feedDoc
					.getXmlVersion());
			assertNotNull(feed.getId());
			assertNull(feed.getGenerator().getAttribute("notHere"));
			assertNotNull(feed.getAuthor("Bill Brown"));
			assertNotNull(feed.getContributor("Bill Brown"));
			assertNotNull(feed.getLink("self"));
			assertNull(feed.getLink("self").getContent());
			assertNull(feed.getLink(null));
			assertNotNull(feed.getId().getAttribute("local:something"));
			assertNull(feed.getId().getAttribute("bunk"));
			assertNull(feed.getCategory("math").getAttribute("anythingWrong"));
			assertNull(feed.getCategory("math").getContent());
			assertNotNull(feed.getIcon().getAttribute("local:testAttr"));
			assertNotNull(feed.getLogo().getAttribute("local:testAttr"));
			assertNotNull(feed.getRights().getAttribute("xmlns"));
			assertNull(feed.getRights().getAttribute("sayWhat"));
			assertNotNull(feed.getUpdated().getDateTime());
			assertNull(feed.getSubtitle().getAttribute("sayWhat"));

			// sort the entries:
			feed = feedDoc.sortEntries(feed, Summary.class, "asc");
			feed = feedDoc.sortEntries(feed, Title.class, "desc");
			feed = feedDoc.sortEntries(feed, Updated.class, "asc");
			// this wont sort anything.
			try {
				feed = feedDoc.sortEntries(feed, Author.class, "asc");
				fail("should not get here.");
			} catch (AtomSpecException e) {
				assertEquals(e.getMessage(),
						"The feed entries cannot be sorted by an invalid type 'Author'.");
			}

			XMLStreamWriter writer = XMLOutputFactory.newInstance()
					.createXMLStreamWriter(
							new FileOutputStream("target/dump1.xml"));
			feedDoc.writeFeedDoc(writer, feed, null, null);

			int fileName = 1;
			for (Entry ent : feed.getEntries()) {
				out = new BufferedWriter(new FileWriter("target/"
						+ (fileName++) + ".xml"));
				out.write(ent.toString());
				out.flush();
				out.close();
				if (ent
						.getId()
						.getAtomUri()
						.equals(
								"http://colorfulsoftware.localhost/colorfulsoftware/projects/atomsphere/atom.xml#About")) {
					Author auth = ent.getAuthor("Bill Brown");
					assertNotNull(ent.getAttribute("xmlns"));
					assertNotNull(auth);
					assertNotNull(auth.getAttribute("local:testAttr"));
					Attribute attr1 = auth.getAttribute("local:testAttr");
					assertFalse(attr1.equals(auth.getAttribute("local:blank")));
					assertNull(auth.getAttribute("local:blank"));
					assertNotNull(auth.getExtension("test:test"));
					assertNull(auth.getExtension("local:bunky"));
					assertNull(ent.getAuthor("some other dude"));
					assertNotNull(ent.getContributor("Bill Brown"));
					assertNull(ent.getContributor("some other dude"));
					assertNotNull(ent.getCategory("science"));
					assertNull(ent.getCategory("nothing"));
					assertNotNull(ent.getLink("alternate"));
					assertNull(ent.getLink("http://www.fakeness.net"));
					assertNotNull(ent.getExtension("local:element"));
					assertNull(ent.getExtension("local:notthere"));
					assertNotNull(ent.getPublished().getAttribute("xmlns"));
					assertNotNull(ent.getPublished().getDateTime());
					assertNull(ent.getSummary().getAttribute("sayWhat"));

					Contributor cont = ent.getContributor("Bill Brown");
					assertNotNull(cont);
					assertNotNull(cont.getExtension("test:test"));
					assertNotNull(cont.getAttribute("local:testAttr"));
					attr1 = cont.getAttribute("local:testAttr");
					assertFalse(attr1.equals(auth.getAttribute("local:blank")));
					assertNull(auth.getAttribute("local:blank"));
					assertNotNull(auth.getExtension("test:test"));
					assertNull(auth.getExtension("local:bunky"));
					assertNull(ent.getAuthor("some other dude"));
					assertNotNull(ent.getContributor("Bill Brown"));
					assertNull(ent.getContributor("some other dude"));
					assertNotNull(ent.getCategory("science"));
					assertNull(ent.getCategory("nothing"));
					assertNotNull(ent.getLink("alternate"));
					assertNull(ent.getLink("http://www.fakeness.net"));
					assertNotNull(ent.getExtension("local:element"));
					assertNull(ent.getExtension("local:notthere"));
				}

				// test the source attribute
				if (ent.getSource() != null) {
					Source source = ent.getSource();
					assertNotNull(source.getAttribute("xmlns:sort"));
					assertNotNull(source.getAuthor("The Minority Directory"));
					assertNotNull(source.getContributor("The People"));
					assertNotNull(source.getCategory("purpose"));
					assertNotNull(source.getLink("self"));
					assertNotNull(source.getExtension("sort:asc"));
				}
			}

			// re read the written feed and check the data.
			feed = feedDoc.readFeedToBean(new File("target/dump1.xml"));

			assertNotNull(feed);
			assertNotNull(feed.getCategories());
			List<Category> cats = feed.getCategories();
			for (Category cat : cats) {
				assertNotNull(cat.getTerm());
				if (!cat.getTerm().getValue().equals("thing")
						&& !cat.getTerm().getValue().equals("undefined")) {
					assertNotNull(cat.getLabel());
					assertNotNull(cat.getScheme());
				}
			}

			List<Link> lnks = feed.getLinks();
			for (Link lnk : lnks) {
				assertNotNull(lnk.getHref());
				assertNotNull(lnk.getHreflang());
				assertNotNull(lnk.getTitle());
				assertNotNull(lnk.getType());
				assertNotNull(lnk.getRel());
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail("Issue building feed doc.");
		}

		// test a plain attribute
		try {
			feedDoc.buildAttribute(null, "null");
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(), "Attribute names SHOULD NOT be blank.");
		}

		Id id = null;
		Title title = null;
		Updated updated = null;
		List<Entry> entries = null;
		try {
			id = feedDoc.buildId(null, "http://www.test.com");
			title = feedDoc.buildTitle("title", null);
			updated = feedDoc.buildUpdated(null, Calendar.getInstance()
					.getTime().toString());
			entries = new LinkedList<Entry>();
			entries.add(feedDoc.buildEntry(id, title, updated, null, null,
					null, null, null, null, null, null, null, null, null));
		} catch (AtomSpecException e1) {
			e1.printStackTrace();
			fail("should not get here.");
		}

		try {// no id
			feedDoc.buildFeed(null, title, updated, null, null, null, null,
					null, null, null, null, null, null, null, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"atom:feed elements MUST contain exactly one atom:id element.");
		}

		try {// no title
			feedDoc.buildFeed(id, null, updated, null, null, null, null, null,
					null, null, null, null, null, null, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"atom:feed elements MUST contain exactly one atom:title element.");
		}

		try {// no updated
			feedDoc.buildFeed(id, title, null, null, null, null, null, null,
					null, null, null, null, null, null, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"atom:feed elements MUST contain exactly one atom:updated element.");
		}

		try {// no authors
			feedDoc.buildFeed(id, title, updated, null, null, null, null, null,
					null, null, null, null, null, null, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"atom:feed elements MUST contain one or more atom:author elements, unless the atom:entry contains an atom:source element that contains an atom:author element or, in an Atom Feed Document, the atom:feed element contains an atom:author element itself.");
		}

		try {// no authors 2
			feedDoc.buildFeed(id, title, updated, null, null, null, null, null,
					null, null, null, null, null, null, entries);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"atom:feed elements MUST contain one or more atom:author elements, unless all of the atom:feed element's child atom:entry elements contain at least one atom:author element.");
		}
	}

	/**
	 * test the getContentType functionality.
	 */
	@Test
	public void testGetContentType() {
		try {
			List<Attribute> attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("type", "image/gif"));

			Title title = feedDoc.buildTitle(null, attrs);
			assertEquals(title.getContentType(),
					AtomTextConstruct.ContentType.OTHER);

			attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("type", "text"));
			title = feedDoc.buildTitle(null, attrs);
			assertEquals(title.getContentType(),
					AtomTextConstruct.ContentType.TEXT);

			attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("type", "html"));
			title = feedDoc.buildTitle(null, attrs);
			assertEquals(title.getContentType(),
					AtomTextConstruct.ContentType.HTML);

			attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("type", "xhtml"));
			title = feedDoc.buildTitle(null, attrs);
			assertEquals(title.getContentType(),
					AtomTextConstruct.ContentType.XHTML);

			attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("src",
					"http://www.colorfulsoftware.com/images/logo.gif"));
			Content content = feedDoc.buildContent(null, attrs);
			assertEquals(content.getContentType(),
					AtomTextConstruct.ContentType.EXTERNAL);

		} catch (AtomSpecException e) {
			e.printStackTrace();
			fail("this shouldn't happen");
		}
	}

	/**
	 * test failure of an empty generator
	 */
	@Test
	public void testGenerator() {
		try {
			feedDoc.buildGenerator(null, "");
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"generator elements SHOULD have either a uri or version attribute or non empty content.");
		}
	}

	/**
	 * test category
	 */
	@Test
	public void testCategory() {
		try {
			List<Attribute> attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("term", null));
			feedDoc.buildCategory(attrs, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Category term attribue SHOULD NOT be blank.");
		}

		try {
			List<Attribute> attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("term", ""));
			feedDoc.buildCategory(attrs, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Category term attribue SHOULD NOT be blank.");
		}

		try {
			List<Attribute> attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("label", "something"));
			feedDoc.buildCategory(attrs, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Category term attribue SHOULD NOT be blank.");
		}

		try {
			List<Attribute> attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("term", "something"));
			Category cat = feedDoc.buildCategory(attrs, "");
			assertTrue(cat != null);
			assertTrue(cat.getContent() == null);
		} catch (Exception e) {
			fail("should not get here.");
		}

		try {
			List<Attribute> attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("term", "something"));
			Category cat = feedDoc.buildCategory(attrs,
					"anything should not throw exception");
			assertTrue(cat != null);
			assertTrue(cat.getContent() != null);
		} catch (Exception e) {
			fail("should not get here.");
		}
	}

	/**
	 * test category
	 */
	@Test
	public void testLink() {
		try {
			List<Attribute> attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("href", null));
			feedDoc.buildLink(attrs, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Link href attribue SHOULD NOT be blank.");
		}

		try {
			List<Attribute> attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("href", ""));
			feedDoc.buildLink(attrs, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Link href attribue SHOULD NOT be blank.");
		}
	}

	/**
	 * test failure of an empty generator
	 */
	@Test
	public void testExtension() {
		try {
			Entry ent = feedDoc.readEntryToBean(expectedEntryExt);
			BufferedWriter out = new BufferedWriter(new FileWriter(
					"target/extension.xml"));
			out.write(ent.toString());
			out.flush();
			out.close();
			Extension ext = ent.getExtension("local:element");
			assertNotNull(ext);
			Attribute attr = ext.getAttribute("xmlns:local");
			assertNotNull(attr);

			FeedWriter feedWriter2 = new FeedWriter();
			XMLStreamWriter writer2 = XMLOutputFactory.newInstance()
					.createXMLStreamWriter(
							new FileOutputStream("target/extension2.xml"));
			List<Entry> entries = new LinkedList<Entry>();
			entries.add(ent);
			feedWriter2.writeEntries(writer2, entries);
			writer2.flush();
			writer2.close();
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"generator elements SHOULD have either a uri or version attribute or non empty content.");
		}
	}

	/**
	 * test sorting entries.
	 */
	@Test
	public void testSortEntries() {
		try {
			List<Entry> entries = new LinkedList<Entry>();
			String entryStr1 = entry1.getTitle().getText();
			String entryStr2 = entry2.getTitle().getText();
			String entryStr3 = entry3.getTitle().getText();
			entries.add(entry1);
			entries.add(entry2);
			entries.add(entry3);
			feed1 = feedDoc.buildFeed(feed1.getId(), feed1.getTitle(), feed1
					.getUpdated(), feed1.getRights(), feed1.getAuthors(), feed1
					.getCategories(), feed1.getContributors(),
					feed1.getLinks(), feed1.getAttributes(), feed1
							.getExtensions(), feed1.getGenerator(), feed1
							.getSubtitle(), feed1.getIcon(), feed1.getLogo(),
					entries);
			assertEquals(Title.class.getSimpleName(), "Title");
			feed1 = feedDoc.sortEntries(feed1, Title.class, "asc");
			for (Entry entry : feed1.getEntries()) {
				assertNotNull(entry);
			}
			List<Entry> entries2 = feed1.getEntries();

			assertNotNull(feed1.getEntry(entryStr1));
			assertNull(feed1.getEntry(entryStr1 + "bunk"));
			assertEquals(entries2.get(0).getTitle().getText(), entryStr1);
			entries2.remove(entries2.get(0));
			assertEquals(entries2.get(0).getTitle().getText(), entryStr2);
			entries2.remove(entries2.get(0));
			assertEquals(entries2.get(0).getTitle().getText(), entryStr3);

			feed1 = feedDoc.sortEntries(feed1, Title.class, "desc");
			for (Entry entry : feed1.getEntries()) {
				assertNotNull(entry);
			}
			entries2 = feed1.getEntries();

			assertEquals(entries2.get(0).getTitle().getText(), entryStr3);
			entries2.remove(entries2.get(0));
			assertEquals(entries2.get(0).getTitle().getText(), entryStr2);
			entries2.remove(entries2.get(0));
			assertEquals(entries2.get(0).getTitle().getText(), entryStr1);

			// test the null entries case.
			feed1 = feedDoc.buildFeed(feed1.getId(), feed1.getTitle(), feed1
					.getUpdated(), feed1.getRights(), feed1.getAuthors(), feed1
					.getCategories(), feed1.getContributors(),
					feed1.getLinks(), feed1.getAttributes(), feed1
							.getExtensions(), feed1.getGenerator(), feed1
							.getSubtitle(), feed1.getIcon(), feed1.getLogo(),
					null);
			feed1 = feedDoc.sortEntries(feed1, Title.class, "desc");
			assertNull(feed1.getEntries());

		} catch (Exception e) {
			e.printStackTrace();
			fail("this shouldn't happen");
		}

		try {
			feed1 = feedDoc.readFeedToBean(basicFeed6);
			assertEquals(feed1.getEntries().get(0).getId().getAtomUri(), "125");
			assertEquals(feed1.getEntries().get(1).getId().getAtomUri(), "123");
		} catch (Exception e) {
			e.printStackTrace();
			fail("this shouldn't happen");
		}
	}

}
