# Goobi workflow

[![Build Status](https://travis-ci.org/intranda/goobi-workflow.svg?branch=master)](https://travis-ci.org/intranda/goobi)

## What is Goobi workflow?

Goobi workflow is an open-source software application for digitisation projects. It allows you to model, manage and supervise freely definable production processes and is used on a daily basis by many institutions to handle all the steps involved in creating a digital library. These include importing data from library catalogues, scanning and content-based indexing and the digital presentation and delivery of results in popular standardised formats – from book to online presentation

![Get an overview of all workflows in digitisation projects with Goobi](https://www.intranda.com/wp-content/uploads/2015/05/goobi_history_21_processes_en.png)

## Community

Goobi workflow is currently used for every day production inside of digitisation projects within approx. 70 cultural heritage institutions in 17 countries. The still quite new community website is located here:

https://goobi.io

Beside this website there is the very active Goobi community talking about different topics:

https://community.goobi.io

The discussion is currently still mostly happening in German. But please feel free to ask any questions there in English too. The community is more than happy to answer these in English of course too.

## Main Documentation

The central documentation space for all topics around Goobi workflow and Goobi viewer is located here:

https://docs.goobi.io/docs/

A complete German documentation regarding Goobi workflow can be found using this URL:

https://docs.goobi.io/goobi-workflow-de/

The English documentation for Goobi workflow is located here:

https://docs.goobi.io/goobi-workflow-en/

## Plugin Documentation

Currently there are about 180 different plugins for Goobi workflow. Not all of those are currently documented. However the new documentation area lists the first new documented plugins here:

German documentation of the plugins:
https://docs.goobi.io/goobi-workflow-plugins-de/

English documentation of the plugins:
https://docs.goobi.io/goobi-workflow-plugins-en/

## Development

The development of Goobi workflow in mostly happening by the software company [intranda GmbH](https://intranda.com) located in Göttingen/Germany. All current developments are centrally listed and explained inside of the monthly digests which can be found here:

German monthly digests:
https://docs.goobi.io/goobi-workflow-digests-de/

English monthly digests:
https://docs.goobi.io/goobi-workflow-digests-en/

## Technical background

The digitisation project management solution Goobi is based on the latest software technology and standards. Goobi uses JavaServer Faces and can be operated independently of any specific platform. The following section contains a detailed overview of the technology involved and of the program’s potential applications.

## Integration

- Flexible import from various OPACs (e.g. using PICA+, MARC21, MAB2, Dublin Core, MODS, LIDO, EAD)
- Flexible data export (e.g. as METS/MODS, METS/MARC, Dublin Core, OAI, MARC, LIDO, EAD)
- Support for different image file formats (e.g. TIF, JPEG, JPEG 2000, PNG, DjVu)
- OCR integration using ABBYY Finereader SDK, Tesseract and ABBYY Recognition Server
- Editor to capture pagination data, structure data and metadata to whatever depth is required
- Integration and control of various scanning devices from different manufacturers
- Individual and automated tasks can be specified in any script language and can be used to extend the workflow.

## Technical parameters

- Development based on JavaServer Faces (JSF) 2.2.
- Support for commonly used Java application servers (e.g. Apache Tomcat, GlassFish)
- Support for commonly used SQL-compliant databases (e.g. MySQL)
- Support for directory services based on the x.500 protocol (e.g. LDAP, Active Directory)
- Not dependent on a single operating system (can be run for example on UNIX systems such as SUN Solaris, BSD systems, Mac OSX, different Linux distributions and Microsoft Windows)
- Goobi uses a range of protocols (e.g. SMB, FTP, SFTP and WebDAV) and is therefore not dependent on a single file system.

## Format diversity

If the output generated by digitisation projects is to be of lasting value, it needs to be in a format that can also be processed by other software tools. For this reason, Goobi employs standardised formats. A list of the formats currently supported is shown below:

### Metadata formats
At present, Goobi supports the following metadata formats:

- METS / MODS
- MARC 21
- MARC XML
- MAB 2
- Dublin Core
- Xepicur
- LIDO
- EAD

### File formats

At present, Goobi supports the following file formats:

- TIFF
- JPEG
- PNG
- JPEG 2000
- PDF
- DjVu
- Open Office
- Microsoft Office

## Searching for more information?

If you are searching for more information please get in touch with the development team of intranda. You can contact them using the web form here:

http://www.intranda.com/en/contact/

If you are interested in more details you can find the release notes here at GitHub unter the following url:

https://github.com/intranda/goobi-workflow/releases
