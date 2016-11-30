msxsl FIX42.xml FieldNumbers.xsl -o ..\src\C++\FieldNumbers.h
msxsl FIX42.xml Fields.xsl -o ..\src\C++\Fields.h
msxsl FIX42.xml Values.xsl -o ..\src\C++\Values.h
msxsl FIX40.xml MessageCracker.xsl -o ..\src\C++\FIX40_MessageCracker.h
msxsl FIX41.xml MessageCracker.xsl -o ..\src\C++\FIX41_MessageCracker.h
msxsl FIX42.xml MessageCracker.xsl -o ..\src\C++\FIX42_MessageCracker.h
msxsl FIX40.xml Messages.xsl -o ..\src\C++\FIX40_Messages.h
msxsl FIX41.xml Messages.xsl -o ..\src\C++\FIX41_Messages.h
msxsl FIX42.xml Messages.xsl -o ..\src\C++\FIX42_Messages.h
msxsl FIX40.xml java_MessageFactory.xsl -o ..\src\java\src\org\quickfix\fix40\MessageFactory.java
msxsl FIX41.xml java_MessageFactory.xsl -o ..\src\java\src\org\quickfix\fix41\MessageFactory.java
msxsl FIX42.xml java_MessageFactory.xsl -o ..\src\java\src\org\quickfix\fix42\MessageFactory.java
msxsl FIX40.xml java_Message.xsl -o ..\src\java\src\org\quickfix\fix40\Message.java
msxsl FIX41.xml java_Message.xsl -o ..\src\java\src\org\quickfix\fix41\Message.java
msxsl FIX42.xml java_Message.xsl -o ..\src\java\src\org\quickfix\fix42\Message.java
msxsl FIX42.xml generate_java_fields.xsl -o generate_java_fields.bat
call generate_java_fields.bat
msxsl FIX40.xml generate_java_messages.xsl -o generate_java_messages40.bat
msxsl FIX41.xml generate_java_messages.xsl -o generate_java_messages41.bat
msxsl FIX42.xml generate_java_messages.xsl -o generate_java_messages42.bat
call generate_java_messages40.bat
call generate_java_messages41.bat
call generate_java_messages42.bat


