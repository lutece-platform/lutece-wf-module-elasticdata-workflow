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

import fr.paris.lutece.plugins.elasticdata.business.AbstractDataObject;

public class WorkflowDataObject extends AbstractDataObject
{

    private String _strId;
    private String _strActionName;
    private String _strWorkflowState;
    private long _lTaskDuration;
    private long _lCompleteDuration;
    private int _nIdResource;
    private String _strResourceType;
    private String _strUserAccessCode;
    private int _nIdWorkflow;
    private String _strWorkflowName;
    private int _nIdAction;

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

    /**
     * Returns the task Duration
     * 
     * @return The task Duration
     */
    public long getTaskDuration( )
    {
        return _lTaskDuration;
    }

    /**
     * Sets the task Duration
     * 
     * @param lTaskDuration
     *            The task Duration
     */
    public void setTaskDuration( long lTaskDuration )
    {
        _lTaskDuration = lTaskDuration;
    }

    /**
     * Returns the full Duration
     * 
     * @return The Duration
     */
    public long getCompleteDuration( )
    {
        return _lCompleteDuration;
    }

    /**
     * Sets the full Duration
     * 
     * @param lCompleteDuration
     *            The full Duration
     */
    public void setCompleteDuration( long lCompleteDuration )
    {
        _lCompleteDuration = lCompleteDuration;
    }

    /**
     * Returns the Resource Type
     * 
     * @return The Resource Type
     */
    public String getResourceType( )
    {
        return _strResourceType;
    }

    /**
     * Sets the Resource Type
     * 
     * @param strResourceType
     *            The Resource Type
     */
    public void setResourceType( String strResourceType )
    {
        _strResourceType = strResourceType;
    }

    /**
     * Returns the Action name
     * 
     * @return The Action Name
     */
    public String getActionName( )
    {
        return _strActionName;
    }

    /**
     * Sets the Action name
     * 
     * @param strActionName
     *            The Action Name
     */
    public void setActionName( String strActionName )
    {
        _strActionName = strActionName;
    }

    /**
     * Returns the Workflow state
     * 
     * @return The Workflow state
     */
    public String getWorkflowState( )
    {
        return _strWorkflowState;
    }

    /**
     * Sets the Workflow state
     * 
     * @param strWorkflowState
     *            The Workflow state
     */
    public void setWorkflowState( String strWorkflowState )
    {
        _strWorkflowState = strWorkflowState;
    }

    /**
     * Returns the UserAccessCode
     * 
     * @return The UserAccessCode
     */
    public String getUserAccessCode( )
    {
        return _strUserAccessCode;
    }

    /**
     * Sets the UserAccessCode
     * 
     * @param strUserAccessCode
     *            The UserAccessCode
     */
    public void setUserAccessCode( String strUserAccessCode )
    {
        _strUserAccessCode = strUserAccessCode;
    }

    public String getWorkflowName( )
    {
        return _strWorkflowName;
    }

    public void setWorkflowName( String strWorkflowName )
    {
        _strWorkflowName = strWorkflowName;
    }

    public int getIdAction( )
    {
        return _nIdAction;
    }

    public void setIdAction( int nIdAction )
    {
        _nIdAction = nIdAction;
    }

    public int getIdWorkflow( )
    {
        return _nIdWorkflow;
    }

    public void setIdWorkflow( int nIdWorkflow )
    {
        _nIdWorkflow = nIdWorkflow;
    }
}
