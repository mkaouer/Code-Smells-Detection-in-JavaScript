/** @jsx React.DOM */

/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant 
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

'use strict';

var React = require('react');

var Router = require('react-router-component');
var InternalLink = Router.Link;
var QuickLinks = require('./QuickLinks');
var ListGroup = require('react-bootstrap/ListGroup');
var ListGroupItem = require('react-bootstrap/ListGroupItem');

var PageContainer = React.createClass({
  render: function () {
    return (
      <div className="col-md-3">
        <div className="bs-docs-section">

          <h3 className="page-header">Quick Links</h3>
          <ul>
            <li><h4><InternalLink href="/overview/">Overview</InternalLink></h4></li>
            <li><h4><InternalLink href="/tables/">Tables</InternalLink></h4></li>
            <li><h4><a href="//github.com/facebook/osquery/wiki" target="_blank">Wiki</a></h4></li>
            <li><h4><a href="//github.com/facebook/osquery/" target="_blank">GitHub</a></h4></li>
          </ul>

          <h3 className="page-header">Guides</h3>
          <ul>
            <li><h4><a href="//github.com/facebook/osquery/wiki/using-osqueryi" target="_blank">Using the Query Console</a></h4></li>
            <li><h4><a href="//github.com/facebook/osquery/wiki/using-osqueryd" target="_blank">Host Monitoring</a></h4></li>
            <li><h4><a href="//github.com/facebook/osquery/wiki/creating-a-new-table" target="_blank">Creating Tables</a></h4></li>
            <li><h4><a href="//github.com/facebook/osquery/wiki/internal-deployment-guide" target="_blank">Deployment Guide</a></h4></li>
          </ul>

          <h3 className="page-header">Community</h3>
          <ul>
            <li><h4><a href="//github.com/facebook/osquery/issues/new" target="_blank">Report an issue</a></h4></li>
            <li><h4><a href="//github.com/facebook/osquery/labels/RFC" target="_blank">Engineering discussions</a></h4></li>
          </ul>

          <h3 className="page-header">License</h3>
          <ul>
            <li><h4><a href="//github.com/facebook/osquery/blob/master/LICENSE" target="_blank">BSD License</a></h4></li>
            <li><h4><a href="//github.com/facebook/osquery/blob/master/CONTRIBUTING.md" target="_blank">Contributing</a></h4></li>
          </ul>
        </div>
      </div>
    );
  }
});

module.exports = PageContainer;
