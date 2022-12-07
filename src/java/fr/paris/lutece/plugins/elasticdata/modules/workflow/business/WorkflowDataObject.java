
/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.elasticdata.modules.workflow.business;

import java.util.List;

import fr.paris.lutece.plugins.elasticdata.business.AbstractDataObject;

public class WorkflowDataObject extends AbstractDataObject
{

    private String _strId;
    private int _nIdResource;
    private int _nIdWorkflow;
    private String _strWorkflowName;
    private String _strLastWorkflowState;
    private List<WorkflowResourceHistory> _listWorkflows;
    private String _strConnectionId;
    private Long _lDateLastUpdate;
    

    /**
     * Returns the id id
     * 
     * @return The id id
     */
    public String getId( )
    {
        return _strId;
    }

    /**
     * Sets the id id
     * 
     * @param strId
     *            The id id
     */
    public void setId( String strId )
    {
        _strId = strId;
    }

    /**
     * Sets the resource id
     * 
     * @param strId
     *            The resource id
     */
    public void setIdResource( int nIdResource )
    {
        _nIdResource = nIdResource;
    }

    /**
     * Returns the resource id
     * 
     * @return The resource id
     */
    public int getIdResource( )
    {
        return _nIdResource;
    }


    public int getIdWorkflow( )
    {
        return _nIdWorkflow;
    }

    public void setIdWorkflow( int nIdWorkflow )
    {
        _nIdWorkflow = nIdWorkflow;
    }

    // set listResource;
    public void setWorkflows( List<WorkflowResourceHistory> listWorkflows )
    {
        _listWorkflows = listWorkflows;
    }

    // get listResource;

    public List<WorkflowResourceHistory> getWorkflows( )
    {
        return _listWorkflows;
    }

    /**
     * Returns the last Workflow state
     * 
     * @return The Last Workflow state
     */
    public String getLastWorkflowState( )
    {
        return _strLastWorkflowState;
    }

    /**
     * Sets the Last Workflow state
     * 
     * @param strLastWorkflowState
     *            The Workflow state
     */
    public void setLastWorkflowState( String strLastWorkflowState )
    {
        _strLastWorkflowState = strLastWorkflowState;
    }

    /**
     * Returns the connection id
     * 
     * @return The connection id
     */
    public String getConnectionId( )
    {
        return _strConnectionId;
    }

    /**
     * Sets the connection id
     * 
     * @param strConnectionId
     *            The connection id
     */

    public void setConnectionId( String strConnectionId )
    {
        _strConnectionId = strConnectionId;
    }


    /**
     * Returns the workflow name
     * 
     * @return The workflow name
     */
    public String getWorkflowName( )
    {
        return _strWorkflowName;
    }

    /**
     * Sets the workflow name
     * 
     * @param strWorkflowName
     *            The workflow name
     */
    public void setWorkflowName( String strWorkflowName )
    {
        _strWorkflowName = strWorkflowName;
    }

    /**
     * Returns the date last update
     * 
     * @return The date last update
     */
    public Long getDateLastUpdate( )
    {
        return _lDateLastUpdate;
    }
    
    /**
     * Sets the date last update
     * 
     * @param lDateLastUpdate
     *            The date last update
     */
    public void setDateLastUpdate( Long lDateLastUpdate )
    {
        _lDateLastUpdate = lDateLastUpdate;
    }
}