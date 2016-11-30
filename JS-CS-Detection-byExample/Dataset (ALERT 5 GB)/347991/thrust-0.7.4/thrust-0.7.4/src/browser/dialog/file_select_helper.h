// Copyright (c) 2014 Stanislas Polu.
// Copyright (c) 2012 The Chromium Authors.
// See the LICENSE file.

#ifndef THRUST_SHELL_BROWSER_UI_DIALOG_FILE_SELECT_HELPER_H_
#define THRUST_SHELL_BROWSER_UI_DIALOG_FILE_SELECT_HELPER_H_

#include <map>
#include <vector>

#include "base/compiler_specific.h"
#include "base/gtest_prod_util.h"
#include "net/base/directory_lister.h"
#include "ui/shell_dialogs/select_file_dialog.h"
#include "content/public/browser/notification_observer.h"
#include "content/public/browser/notification_registrar.h"
#include "content/public/common/file_chooser_params.h"

namespace content {
class RenderViewHost;
class WebContents;
}

namespace ui {
struct SelectedFileInfo;
}

namespace thrust_shell {

// This class handles file-selection requests coming from WebUI elements
// (via the extensions::ExtensionHost class). It implements both the
// initialisation and listener functions for file-selection dialogs.
class FileSelectHelper
    : public base::RefCountedThreadSafe<FileSelectHelper>,
      public ui::SelectFileDialog::Listener,
      public content::NotificationObserver {
 public:

  // Show the file chooser dialog.
  static void RunFileChooser(content::WebContents* tab,
                             const content::FileChooserParams& params);

  // Enumerates all the files in directory.
  static void EnumerateDirectory(content::WebContents* tab,
                                 int request_id,
                                 const base::FilePath& path);

 private:
  explicit FileSelectHelper();
  virtual ~FileSelectHelper();

  // Utility class which can listen for directory lister events and relay
  // them to the main object with the correct tracking id.
  class DirectoryListerDispatchDelegate
      : public net::DirectoryLister::DirectoryListerDelegate {
   public:
    DirectoryListerDispatchDelegate(FileSelectHelper* parent, int id)
        : parent_(parent),
          id_(id) {}
    virtual ~DirectoryListerDispatchDelegate() {}
    virtual void OnListFile(
        const net::DirectoryLister::DirectoryListerData& data) OVERRIDE;
    virtual void OnListDone(int error) OVERRIDE;
   private:
    // This FileSelectHelper owns this object.
    FileSelectHelper* parent_;
    int id_;

    DISALLOW_COPY_AND_ASSIGN(DirectoryListerDispatchDelegate);
  };

  void RunFileChooser(content::RenderViewHost* render_view_host,
                      content::WebContents* web_contents,
                      const content::FileChooserParams& params);
  void RunFileChooserOnFileThread(
      const content::FileChooserParams& params);
  void RunFileChooserOnUIThread(
      const content::FileChooserParams& params);

  // Cleans up and releases this instance. This must be called after the last
  // callback is received from the file chooser dialog.
  void RunFileChooserEnd();

  // SelectFileDialog::Listener overrides.
  virtual void FileSelected(
      const base::FilePath& path, int index, void* params) OVERRIDE;
  virtual void FileSelectedWithExtraInfo(
      const ui::SelectedFileInfo& file,
      int index,
      void* params) OVERRIDE;
  virtual void MultiFilesSelected(const std::vector<base::FilePath>& files,
                                  void* params) OVERRIDE;
  virtual void MultiFilesSelectedWithExtraInfo(
      const std::vector<ui::SelectedFileInfo>& files,
      void* params) OVERRIDE;
  virtual void FileSelectionCanceled(void* params) OVERRIDE;

  // content::NotificationObserver overrides.
  virtual void Observe(int type,
                       const content::NotificationSource& source,
                       const content::NotificationDetails& details) OVERRIDE;

  void EnumerateDirectory(int request_id,
                          content::RenderViewHost* render_view_host,
                          const base::FilePath& path);

  // Kicks off a new directory enumeration.
  void StartNewEnumeration(const base::FilePath& path,
                           int request_id,
                           content::RenderViewHost* render_view_host);

  // Callbacks from directory enumeration.
  virtual void OnListFile(
      int id,
      const net::DirectoryLister::DirectoryListerData& data);
  virtual void OnListDone(int id, int error);

  // Cleans up and releases this instance. This must be called after the last
  // callback is received from the enumeration code.
  void EnumerateDirectoryEnd();

  // Helper method to get allowed extensions for select file dialog from
  // the specified accept types as defined in the spec:
  //   http://whatwg.org/html/number-state.html#attr-input-accept
  // |accept_types| contains only valid lowercased MIME types or file extensions
  // beginning with a period (.).
  static scoped_ptr<ui::SelectFileDialog::FileTypeInfo>
      GetFileTypesFromAcceptType(
          const std::vector<base::string16>& accept_types);

  // Check the accept type is valid. It is expected to be all lower case with
  // no whitespace.
  static bool IsAcceptTypeValid(const std::string& accept_type);

  // The RenderViewHost and WebContents for the page showing a file dialog
  // (may only be one such dialog).
  content::RenderViewHost* render_view_host_;
  content::WebContents* web_contents_;

  // Dialog box used for choosing files to upload from file form fields.
  scoped_refptr<ui::SelectFileDialog> select_file_dialog_;
  scoped_ptr<ui::SelectFileDialog::FileTypeInfo> select_file_types_;

  // The type of file dialog last shown.
  ui::SelectFileDialog::Type dialog_type_;

  // The mode of file dialog last shown.
  content::FileChooserParams::Mode dialog_mode_;

  // Maintain a list of active directory enumerations.  These could come from
  // the file select dialog or from drag-and-drop of directories, so there could
  // be more than one going on at a time.
  struct ActiveDirectoryEnumeration;
  std::map<int, ActiveDirectoryEnumeration*> directory_enumerations_;

  // Registrar for notifications regarding our RenderViewHost.
  content::NotificationRegistrar notification_registrar_;

  friend class base::RefCountedThreadSafe<FileSelectHelper>;

  DISALLOW_COPY_AND_ASSIGN(FileSelectHelper);
};

} // namespace thrust_shell

#endif // THRUST_SHELL_BROWSER_UI_DIALOG_FILE_SELECT_HELPER_H_
