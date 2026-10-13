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

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.elasticdata.business.DataObject;
import fr.paris.lutece.plugins.elasticdata.business.IndexerAction;
import fr.paris.lutece.plugins.elasticdata.business.IndexerActionHome;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.icon.IIconService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Tests of the workflow data source
 */
public class WorkflowDataSourceTest extends LuteceTestCase
{
    private static final int ID_RESOURCE = 987654;
    private static final String RESOURCE_TYPE = "ELASTICDATA_WORKFLOW_TEST";
    private static final long DURATION = 5000L;

    @Inject
    private WorkflowDataSource _dataSource;
    @Inject
    private IWorkflowService _workflowService;
    @Inject
    private IStateService _stateService;
    @Inject
    private IActionService _actionService;
    @Inject
    private IIconService _iconService;
    @Inject
    private IResourceWorkflowService _resourceWorkflowService;
    @Inject
    private IResourceHistoryService _resourceHistoryService;

    private Workflow _workflow;
    private State _stateInitial;
    private State _stateDone;
    private Action _action;
    private Icon _icon;
    private ResourceWorkflow _resourceWorkflow;
    private ResourceHistory _historyFirst;
    private ResourceHistory _historyLast;

    /**
     * Creates a workflow with one resource and two history entries
     */
    @BeforeEach
    public void createWorkflowResource( )
    {
        _workflow = new Workflow( );
        _workflow.setName( "Elasticdata workflow test" );
        _workflow.setDescription( "Elasticdata workflow test" );
        _workflow.setCreationDate( new Timestamp( System.currentTimeMillis( ) ) );
        _workflow.setWorkgroup( "all" );
        _workflow.setEnabled( true );
        _workflowService.create( _workflow );

        _stateInitial = createState( "Received", true );
        _stateDone = createState( "Processed", false );

        _icon = new Icon( );
        _icon.setName( "elasticdata-workflow-test" );
        _iconService.create( _icon );

        _action = new Action( );
        _action.setName( "Process" );
        _action.setDescription( "Process" );
        _action.setWorkflow( _workflow );
        _action.setStateAfter( _stateDone );
        _action.setListIdStateBefore( List.of( _stateInitial.getId( ) ) );
        _action.setIcon( _icon );
        _action.setAutomaticState( false );
        _actionService.create( _action );

        _resourceWorkflow = new ResourceWorkflow( );
        _resourceWorkflow.setIdResource( ID_RESOURCE );
        _resourceWorkflow.setResourceType( RESOURCE_TYPE );
        _resourceWorkflow.setWorkFlow( _workflow );
        _resourceWorkflow.setState( _stateDone );
        _resourceWorkflowService.create( _resourceWorkflow );

        long lStart = System.currentTimeMillis( ) - DURATION * 2;
        _historyFirst = createHistory( new Timestamp( lStart ) );
        _historyLast = createHistory( new Timestamp( lStart + DURATION ) );
    }

    /**
     * Removes the test data
     */
    @AfterEach
    public void removeWorkflowResource( )
    {
        _resourceHistoryService.remove( _historyFirst.getId( ) );
        _resourceHistoryService.remove( _historyLast.getId( ) );
        _resourceWorkflowService.remove( _resourceWorkflow );
        _actionService.remove( _action.getId( ) );
        _iconService.remove( _icon.getId( ) );
        _stateService.remove( _stateInitial.getId( ) );
        _stateService.remove( _stateDone.getId( ) );
        _workflowService.remove( _workflow.getId( ) );
        IndexerAction indexerAction = IndexerActionHome.findByIdResource( String.valueOf( ID_RESOURCE ), _dataSource.getId( ) );
        if ( indexerAction != null )
        {
            IndexerActionHome.remove( indexerAction.getId( ) );
        }
    }

    /**
     * The data source is configured from the plugin properties
     */
    @Test
    public void testConfiguration( )
    {
        assertEquals( "WorkflowDataSource", _dataSource.getId( ) );
        assertEquals( "workflowdatasource", _dataSource.getTargetIndexName( ) );
        assertTrue( _dataSource.getMappings( ).contains( "dateLastUpdate" ) );
    }

    /**
     * The resource of the workflow is listed and its history is built
     */
    @Test
    public void testGetDataObjects( )
    {
        assertTrue( _dataSource.getIdDataObjects( ).contains( String.valueOf( ID_RESOURCE ) ) );

        List<DataObject> listDataObjects = _dataSource.getDataObjects( List.of( String.valueOf( ID_RESOURCE ) ) );

        assertEquals( 1, listDataObjects.size( ) );
        WorkflowDataObject dataObject = (WorkflowDataObject) listDataObjects.get( 0 );
        assertEquals( String.valueOf( ID_RESOURCE ), dataObject.getId( ) );
        assertEquals( ID_RESOURCE, dataObject.getIdResource( ) );
        assertEquals( _workflow.getId( ), dataObject.getIdWorkflow( ) );
        assertEquals( "Elasticdata workflow test", dataObject.getWorkflowName( ) );
        assertEquals( "Processed", dataObject.getLastWorkflowState( ) );
        assertEquals( Long.valueOf( _historyLast.getCreationDate( ).getTime( ) ), dataObject.getDateLastUpdate( ) );
        assertEquals( 2, dataObject.getWorkflows( ).size( ) );
        WorkflowResourceHistory history = dataObject.getWorkflows( ).get( 1 );
        assertEquals( "Process", history.getActionName( ) );
        assertEquals( RESOURCE_TYPE, history.getResourceType( ) );
        assertEquals( "admin", history.getUserAccessCode( ) );
        assertEquals( DURATION, history.getCompleteDuration( ) );
    }

    /**
     * The incremental indexing queues a task for the resource
     */
    @Test
    public void testIndexDocument( )
    {
        _dataSource.indexDocument( ID_RESOURCE, IndexerAction.TASK_CREATE );

        IndexerAction indexerAction = IndexerActionHome.findByIdResource( String.valueOf( ID_RESOURCE ), _dataSource.getId( ) );
        assertNotNull( indexerAction );
        assertEquals( IndexerAction.TASK_CREATE, indexerAction.getIdTask( ) );
    }

    /**
     * Creates a state of the test workflow
     *
     * @param strName
     *            the state name
     * @param bInitial
     *            true for the initial state
     * @return the created state
     */
    private State createState( String strName, boolean bInitial )
    {
        State state = new State( );
        state.setName( strName );
        state.setDescription( strName );
        state.setWorkflow( _workflow );
        state.setInitialState( bInitial );
        state.setRequiredWorkgroupAssigned( false );
        _stateService.create( state );
        return state;
    }

    /**
     * Creates a history entry of the test resource
     *
     * @param date
     *            the creation date
     * @return the created history
     */
    private ResourceHistory createHistory( Timestamp date )
    {
        ResourceHistory history = new ResourceHistory( );
        history.setIdResource( ID_RESOURCE );
        history.setResourceType( RESOURCE_TYPE );
        history.setWorkFlow( _workflow );
        history.setAction( _action );
        history.setCreationDate( date );
        history.setUserAccessCode( "admin" );
        _resourceHistoryService.create( history );
        return history;
    }
}
