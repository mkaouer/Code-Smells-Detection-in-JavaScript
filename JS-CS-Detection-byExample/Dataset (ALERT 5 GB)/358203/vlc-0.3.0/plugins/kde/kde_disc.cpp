/***************************************************************************
                          kde_disc.cpp  -  description
                             -------------------
    begin                : Sat Apr 7 2001
    copyright            : (C) 2001 by andres
    email                : dae@chez.com
 ***************************************************************************/

#include "kde_disc.h"

#include <qhbox.h>
#include <qlabel.h>
#include <qradiobutton.h>
#include <qspinbox.h>
#include <qstring.h>
#include <qvbox.h>
#include <qvbuttongroup.h>
#include <qvgroupbox.h>
#include <qwidget.h>
#include <kdialogbase.h>
#include <klineedit.h>

KDiskDialog::KDiskDialog( QWidget *parent, const char *name )
            :KDialogBase( parent, name, true, QString::null,
                          Ok|Cancel, Ok, true )
{
    QVBox *pageVBox = makeVBoxMainWidget();

    QHBox *deviceSelectHBox = new QHBox( pageVBox );
    deviceSelectHBox->setSpacing( 5 );
    fButtonGroup = new QVButtonGroup( "Disk type", deviceSelectHBox );
    fDVDButton = new QRadioButton( "DVD", fButtonGroup);
    fDVDButton->setChecked( true );
    fVCDButton = new QRadioButton( "VCD", fButtonGroup);
    fVCDButton->setEnabled( false );

    QVGroupBox *startVBox = new QVGroupBox( "Starting position", deviceSelectHBox );

    QHBox *titleHBox = new QHBox( startVBox );
    QLabel *titleLabel = new QLabel( "Title ", titleHBox );
    fTitle = new QSpinBox( titleHBox );
    QHBox *chapterHBox = new QHBox( startVBox );
    QLabel *chapterLabel = new QLabel( "Chapter ", chapterHBox );
    fChapter = new QSpinBox( chapterHBox );

    QHBox *deviceNameHBox = new QHBox( pageVBox );
    QLabel *deviceNameLabel = new QLabel( "Device name ", deviceNameHBox );
    fLineEdit = new KLineEdit( "/dev/dvd", deviceNameHBox );
}

KDiskDialog::~KDiskDialog()
{
}

QString KDiskDialog::type() const
{
    if ( fDVDButton->isChecked() )
    {
        return ( QString("dvd") );
    }
    else
    {
        return ( QString("vcd") );
    }
}

QString KDiskDialog::device() const
{
    return ( fLineEdit->text() );
}

int KDiskDialog::title() const
{
    return ( fTitle->value() );
}

int KDiskDialog::chapter() const
{
    return ( fChapter->value() );
}
