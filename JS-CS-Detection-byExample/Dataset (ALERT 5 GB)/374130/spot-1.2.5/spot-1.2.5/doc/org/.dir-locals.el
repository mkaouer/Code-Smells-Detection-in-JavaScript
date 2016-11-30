((nil . ((whitespace-action auto-cleanup)
	 (whitespace-style face empty trailing lines-tail)
	 (require-final-newline . t)
	 (mode . whitespace)))
 (org-mode . ((whitespace-style face empty trailing)
	      (eval .
		    (progn
		      (setq org-babel-sh-command (concat "PATH=../../src/bin"
							 path-separator
							 "$PATH sh"))
		      (org-babel-do-load-languages 'org-babel-load-languages
						   '((sh . t)
						     (python . t)
						     (dot . t)))))
	      (org-confirm-babel-evaluate . nil)
	      (org-babel-python-command . "/usr/bin/python3")
	      (org-publish-project-alist
	       . (("spot-html"
		   :base-directory "."
		   :base-extension "org"
		   :publishing-directory "../userdoc/"
		   :recursive t
		   :publishing-function org-publish-org-to-html
		   :style "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://spot.lip6.fr/css/spot-zenburn.css\" />"
		   :auto-preamble t)
		  ("spot-static"
		   :base-directory "."
		   :base-extension "css\\|js\\|png\\|jpg\\|gif\\|pdf"
		   :publishing-directory "../userdoc/"
		   :recursive t
		   :publishing-function org-publish-attachment)
		  ("spot-all" :components ("spot-html" "spot-static")))))))
