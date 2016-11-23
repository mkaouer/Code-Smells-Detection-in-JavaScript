/*
 * Copyright (c) 2003-2007 Rony Shapiro <ronys@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license.php
 */
// XMLprefs.cpp : implementation file
//
#include "XMLprefs.h"
#include "tinyxml/tinyxml.h"
#include "MyString.h"
#include "PWSprefs.h"
#include "corelib.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

//#define DEBUG_XMLPREFS
#ifdef DEBUG_XMLPREFS
#include <stdio.h>
static FILE *f;
#define DOPEN() do {f = fopen("cxmlpref.log", "a+");} while (0)
#define DPRINT(x) do {fprintf x; fflush(f);} while (0)
#define DCLOSE() fclose(f)
#else
#define DOPEN()
#define DPRINT(x)
#define DCLOSE()
#endif
/////////////////////////////////////////////////////////////////////////////
// CXMLprefs

bool CXMLprefs::Lock()
{
	CMyString locker(_T(""));
    int tries = 10;
    do {
        m_bIsLocked = PWSprefs::LockCFGFile(m_csConfigFile, locker);
        if (!m_bIsLocked)
            Sleep(200);
    } while (!m_bIsLocked && --tries > 0);
    return m_bIsLocked;
}

void CXMLprefs::Unlock()
{
    PWSprefs::UnlockCFGFile(m_csConfigFile);
    m_bIsLocked = false;
}

bool CXMLprefs::CreateXML(bool forLoad)
{
    // Call with forLoad set when about to Load, else
    // this also adds a toplevel root element
    ASSERT(m_pXMLDoc == NULL);
    m_pXMLDoc = new TiXmlDocument(m_csConfigFile);
    if (!forLoad && m_pXMLDoc != NULL) {
        TiXmlDeclaration decl(_T("1.0"), _T("UTF-8"), _T("yes"));
        TiXmlElement rootElem(_T("Pwsafe_Settings"));

        return (m_pXMLDoc->InsertEndChild(decl) != NULL &&
                m_pXMLDoc->InsertEndChild(rootElem) != NULL);
    } else
        return m_pXMLDoc != NULL;
}

bool CXMLprefs::Load()
{
	// Already loaded?
	if (m_pXMLDoc != NULL) return true;
    DOPEN();
    DPRINT((f, "Entered CXMLprefs::Load()\n"));

    bool alreadyLocked = m_bIsLocked;
    if (!alreadyLocked) {
        if (!Lock())
            return false;
    }

    if (!CreateXML(true))
        return false;

    bool retval = m_pXMLDoc->LoadFile();

	if (!retval) {
        // an XML load error occurred so display the reason
        CString csMessage;
        csMessage.Format(IDSC_XMLFILEERROR,
                         m_pXMLDoc->ErrorDesc(), m_csConfigFile,
                         m_pXMLDoc->ErrorRow(), m_pXMLDoc->ErrorCol());
        const CString cs_title(MAKEINTRESOURCE(IDSC_XMLLOADFAILURE));
        MessageBox(NULL, csMessage, cs_title, MB_OK);
        
        delete m_pXMLDoc;
        m_pXMLDoc = NULL;
    } // load failed

    // if we locked it, we should unlock it...
    if (!alreadyLocked)
        Unlock();
    DPRINT((f, "Leaving CXMLprefs::Load(), retval = %s\n",
            retval ? "true" : "false"));
    DCLOSE();
    return retval;
}

bool CXMLprefs::Store()
{
	bool retval = false;
    bool alreadyLocked = m_bIsLocked;

    if (!alreadyLocked) {
        if (!Lock())
            return false;
    }

    DOPEN();
    DPRINT((f, "Entered CXMLprefs::Store()\n"));
    DPRINT((f, "\tm_pXMLDoc = %p\n", m_pXMLDoc));

    // Although technically possible, it doesn't make sense
    // to create a toplevel document here, since we'd then
    // be saving an empty document.
    ASSERT(m_pXMLDoc != NULL);
    if (m_pXMLDoc == NULL) {
        retval = false;
        goto exit;
    }

    retval = m_pXMLDoc->SaveFile();
    if (!retval) {
        // Get and show error
        CString csMessage;
        csMessage.Format(IDSC_XMLFILEERROR,
                         m_pXMLDoc->ErrorDesc(), m_csConfigFile,
                         m_pXMLDoc->ErrorRow(), m_pXMLDoc->ErrorCol());
        const CString cs_title(MAKEINTRESOURCE(IDSC_XMLSAVEFAILURE));
        MessageBox(NULL, csMessage, cs_title, MB_OK);
    }

  exit:
    // if we locked it, we should unlock it...
    if (!alreadyLocked)
        Unlock();
    DPRINT((f, "Leaving CXMLprefs::Store(), retval = %s\n",
            retval ? "true" : "false"));
    DCLOSE();
    return retval;
}


// get a int value
int CXMLprefs::Get(const CString &csBaseKeyName, const CString &csValueName, 
					   const int &iDefaultValue)
{
	/*
		Since XML is text based and we have no schema, just convert to a string and
		call the GetSettingString method.
	*/
	int iRetVal = iDefaultValue;
	CString csDefaultValue;

	csDefaultValue.Format(_T("%d"), iRetVal);

	iRetVal = _ttoi(Get(csBaseKeyName, csValueName, csDefaultValue));

	return iRetVal;
}

// get a string value
CString CXMLprefs::Get(const CString &csBaseKeyName, const CString &csValueName, 
                       const CString &csDefaultValue)
{
    ASSERT(m_pXMLDoc != NULL); // shouldn't be called if not loaded
    if (m_pXMLDoc == NULL) // just in case
        return csDefaultValue;

	int iNumKeys = 0;
	CString csValue = csDefaultValue;

	// Add the value to the base key separated by a '\'
	CString csKeyName(csBaseKeyName);
	csKeyName += _T("\\");
	csKeyName += csValueName;

	// Parse all keys from the base key name (keys separated by a '\')
	CString *pcsKeys = ParseKeys(csKeyName, iNumKeys);

	// Traverse the xml using the keys parsed from the base key name to find the correct node
	if (pcsKeys != NULL) {
        TiXmlElement *rootElem = m_pXMLDoc->RootElement();

        if (rootElem != NULL) {
            // returns the last node in the chain
            TiXmlElement *foundNode = FindNode(rootElem, pcsKeys, iNumKeys);

            if (foundNode != NULL) {
                // get the text of the node (will be the value we requested)
                csValue = CString(foundNode->GetText());
            }
        }
		delete[] pcsKeys;
	}

	return csValue;
}

// set a int value
int CXMLprefs::Set(const CString &csBaseKeyName, const CString &csValueName,
					   const int &iValue)
{
	/*
		Since XML is text based and we have no schema, just convert to a string and
		call the SetSettingString method.
	*/
	int iRetVal = 0;
	CString csValue = _T("");

	csValue.Format(_T("%d"), iValue);

	iRetVal = Set(csBaseKeyName, csValueName, csValue);

	return iRetVal;
}

// set a string value
int CXMLprefs::Set(const CString &csBaseKeyName, const CString &csValueName, 
                   const CString &csValue)
{
    // m_pXMLDoc may be NULL if Load() not called b4 Set,
    // or if called & failed
    
    if (m_pXMLDoc == NULL && !CreateXML(false))
        return false;

	int iRetVal = XML_SUCCESS;
	int iNumKeys = 0;

	// Add the value to the base key separated by a '\'
	CString csKeyName(csBaseKeyName);
	csKeyName += _T("\\");
	csKeyName += csValueName;

	// Parse all keys from the base key name (keys separated by a '\')
	CString *pcsKeys = ParseKeys(csKeyName, iNumKeys);

	// Traverse the xml using the keys parsed from the base key name to find the correct node
	if (pcsKeys != NULL) {
        TiXmlElement *rootElem = m_pXMLDoc->RootElement();

        if (rootElem != NULL) {
            // returns the last node in the chain
            TiXmlElement *foundNode = FindNode(rootElem, pcsKeys, iNumKeys, TRUE);

            if (foundNode != NULL) {
                TiXmlNode *valueNode = foundNode->FirstChild();
                if (valueNode != NULL) // replace existing value
                    valueNode->SetValue(csValue);
                else {// first time set
                    TiXmlText value(csValue);
                    foundNode->InsertEndChild(value);
                }
            } else
                iRetVal = XML_NODE_NOT_FOUND;

		} else
			iRetVal = XML_LOAD_FAILED;

		delete [] pcsKeys;
	}
    return iRetVal;
}

// delete a key or chain of keys
BOOL CXMLprefs::DeleteSetting(const CString &csBaseKeyName, const CString &csValueName)
{
    // m_pXMLDoc may be NULL if Load() not called b4 DeleteSetting,
    // or if called & failed
    
    if (m_pXMLDoc == NULL && !CreateXML(false))
        return false;

	BOOL bRetVal = FALSE;
	int iNumKeys = 0;
	CString csKeyName(csBaseKeyName);

	if (!csValueName.IsEmpty()) {
		csKeyName += _T("\\");
		csKeyName += csValueName;
	}

	// Parse all keys from the base key name (keys separated by a '\')
	CString *pcsKeys = ParseKeys(csKeyName, iNumKeys);

	// Traverse the xml using the keys parsed from the base key name to find the correct node.
	if (pcsKeys != NULL) {
        TiXmlElement *rootElem = m_pXMLDoc->RootElement();

        if (rootElem != NULL) {
            // returns the last node in the chain
            TiXmlElement *foundNode = FindNode(rootElem, pcsKeys, iNumKeys);

            if (foundNode!= NULL) {
                // get the parent of the found node and use removeChild to delete the found node
                TiXmlNode *parentNode = foundNode->Parent();

                if (parentNode != NULL) {
                    if (parentNode->RemoveChild(foundNode)) {
                        bRetVal = TRUE;
                    }
                }
            }
		}
		delete[] pcsKeys;
	}
	return bRetVal;
}

// Parse all keys from the base key name.
CString* CXMLprefs::ParseKeys(const CString &csFullKeyPath, int &iNumKeys)
{
	CString* pcsKeys = NULL;

	// replace spaces with _ since xml doesn't like them
	CString csFKP(csFullKeyPath);
	csFKP.Replace(_T(' '), _T('_'));

	if (csFKP.GetAt(csFKP.GetLength() - 1) == _T('\\'))
		csFKP.TrimRight(_T('\\'));  // remove slashes on the end

	CString csTemp(csFKP);

	iNumKeys = csTemp.Remove(_T('\\')) + 1;  // get a count of slashes

	pcsKeys = new CString[iNumKeys];  // create storage for the keys

	if (pcsKeys) {
		int iFind = 0, iLastFind = 0, iCount = -1;

		// get all of the keys in the chain
		while (iFind != -1) {
			iFind = csFKP.Find(_T("\\"), iLastFind);
			if (iFind > -1) {
				iCount++;
				pcsKeys[iCount] = csFKP.Mid(iLastFind, iFind - iLastFind);
				iLastFind = iFind + 1;
			} else {
				// make sure we don't just discard the last key in the chain
				if (iLastFind < csFKP.GetLength())  {
					iCount++;
					pcsKeys[iCount] = csFKP.Right(csFKP.GetLength() - iLastFind);
				}
			}
		}
	}
	return pcsKeys;
}

void CXMLprefs::UnloadXML()
{
	if (m_pXMLDoc != NULL) {
		delete m_pXMLDoc;
		m_pXMLDoc = NULL;
	}
}



// find a node given a chain of key names
TiXmlElement *CXMLprefs::FindNode(TiXmlElement *parentNode,
									CString* pcsKeys, int iNumKeys,
									bool bAddNodes /*= false*/)
{
    ASSERT(m_pXMLDoc != NULL); // shouldn't be called if load failed
    if (m_pXMLDoc == NULL) // just in case
        return NULL;

	for (int i=0; i<iNumKeys; i++) {
		// find the node named X directly under the parent
        TiXmlNode *foundNode = parentNode->IterateChildren(pcsKeys[i], NULL);

		if (foundNode == NULL) {
			// if its not found...
			if (bAddNodes)  {  // create the node and append to parent (Set only)
                TiXmlElement elem(pcsKeys[i]);
                // Add child, set parent to it for next iteration
                parentNode = parentNode->InsertEndChild(elem)->ToElement();
			} else {
				parentNode = NULL;
				break;
			}
		} else {
			// since we are traversing the nodes, we need to set the parentNode to our foundNode
			parentNode = foundNode->ToElement();
			foundNode = NULL;
		}
	}
	return parentNode;
}
