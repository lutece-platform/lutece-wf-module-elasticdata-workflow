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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.inject.Inject;
import fr.paris.lutece.plugins.elasticdata.business.AbstractDataSource;
import fr.paris.lutece.plugins.elasticdata.business.DataObject;
import fr.paris.lutece.plugins.elasticdata.service.DataSourceIncrementalService;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistoryFilter;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceUserHistory;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;

public class WorkflowDataSource extends AbstractDataSource
{
    @Inject
    private IActionService _actionService;
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private IStateService _stateService;
    @Inject
    private IWorkflowService _workflowService;
    @Inject
    private IResourceWorkflowService _resourceWorkflowService;
    private static final String DATA_SOURCE_NAME = "WorkflowDataSource";

    @Override
    public List<String> getIdDataObjects( )
    {
        List<Integer> idResourceList = new ArrayList<>( );
        for ( Workflow wf : getWorkflowList( ) )
        {
            idResourceList.addAll( _resourceWorkflowService.getAllResourceIdByWorkflow( wf.getId( ) ) );
        }
        return idResourceList.stream( ).map( idResource -> String.valueOf( idResource ) ).collect( Collectors.toList( ) );
    }

    @Override
    public List<DataObject> getDataObjects( List<String> listIdResource )
    {
        List<DataObject> collResult = new Vector<>( );
        List<ResourceHistory> listResourceHistory = getResourceHistoryList( listIdResource );
        Map<Integer, List<ResourceHistory>> listWorkflowResourceHistory = listResourceHistory.stream( )
                .collect( Collectors.groupingBy( rh -> rh.getWorkflow( ).getId( ) ) );
        List<Workflow> listWorkflow = getWorkflowList( );

        for ( Map.Entry<Integer, List<ResourceHistory>> entry : listWorkflowResourceHistory.entrySet( ) )
        {
            int nIdWorkflow = entry.getKey( );
            Workflow wf = listWorkflow.stream( ).filter( wf2 -> wf2.getId( ) == nIdWorkflow ).findFirst( ).orElse( null );
            String strWorkflowName = wf.getName( );
            List<State> listStates = getStateList( nIdWorkflow );
            List<Action> listActions = getActionList( nIdWorkflow );
            Map<Integer, List<ResourceHistory>> listResourceHistorybyIdResource = entry.getValue( ).stream( )
                    .collect( Collectors.groupingBy( rh -> rh.getIdResource( ) ) );

            listResourceHistorybyIdResource.values( ).parallelStream( ).forEach( listResourceHistoryIdResource -> {
                Map<String, List<ResourceHistory>> listResourceHistoryByType = listResourceHistoryIdResource.stream( )
                        .collect( Collectors.groupingBy( rh -> rh.getResourceType( ) ) );
                listResourceHistoryByType.values( ).stream( ).forEach( listHistory -> {
                    List<ResourceHistory> listHistoryOrdered = listHistory.stream( ).sorted( Comparator.comparing( ResourceHistory::getCreationDate ) )
                            .collect( Collectors.toList( ) );
                    Timestamp lStartingResource = listHistoryOrdered.get( 0 ).getCreationDate( );
                    Timestamp lstartingDateDuration = lStartingResource;
                    for ( ResourceHistory rh : listHistoryOrdered )
                    {
                        WorkflowDataObject workflowDataObject = new WorkflowDataObject( );
                        long lTaskDuration = duration( lstartingDateDuration, rh.getCreationDate( ) );
                        long lCompleteDuration = duration( lStartingResource, rh.getCreationDate( ) );
                        ResourceUserHistory ruh = rh.getResourceUserHistory( );
                        workflowDataObject.setId( String.valueOf( rh.getId( ) ) );
                        workflowDataObject.setTimestamp( rh.getCreationDate( ).getTime( ) );
                        workflowDataObject.setIdResource( rh.getIdResource( ) );
                        workflowDataObject.setResourceType( rh.getResourceType( ) );
                        workflowDataObject.setUserAccessCode( ruh.getUserAccessCode( ) );
                        workflowDataObject.setIdWorkflow( nIdWorkflow );
                        workflowDataObject.setWorkflowName( strWorkflowName );
                        workflowDataObject.setTaskDuration( lTaskDuration );
                        workflowDataObject.setCompleteDuration( lCompleteDuration );
                        Action action = listActions.stream( ).filter( ac -> ac.getId( ) == rh.getAction( ).getId( ) ).findFirst( ).orElse( null );
                        if ( action != null )
                        {
                            workflowDataObject.setActionName( action.getName( ) );
                            workflowDataObject.setIdAction( action.getId( ) );
                            State stateResourceHistory = listStates.stream( ).filter( st -> st.getId( ) == action.getStateAfter( ).getId( ) ).findFirst( )
                                    .orElse( null );
                            if ( stateResourceHistory != null )
                            {
                                workflowDataObject.setWorkflowState( stateResourceHistory.getName( ) );
                            }
                        }
                        collResult.add( workflowDataObject );
                    }
                } );
            } );
        }
        return collResult;
    }

    /**
     * Index the worflow resource history to Elasticdata ( incremental )
     * 
     * @param nIdResource
     *            The id resource
     * @param nIdTask
     *            The task id
     */
    public void indexDocument( int nIdResource, int nIdTask )
    {
        DataSourceIncrementalService.addTask( DATA_SOURCE_NAME, String.valueOf( nIdResource ), nIdTask );
    }

    /**
     * return a list of resource history according to workflow id
     * 
     * @param nIdWorkflow
     *            the workflow id
     * @return list of ressource history
     */
    private List<ResourceHistory> getResourceHistoryList( List<String> listIdResource )
    {
        ResourceHistoryFilter rhf = new ResourceHistoryFilter( );
        rhf.setListIdResources( listIdResource.stream( ).map( idResource -> Integer.valueOf( idResource ) ).collect( Collectors.toList( ) ) );
        return _resourceHistoryService.getAllHistoryByFilter( rhf ).stream( ).sorted( Comparator.comparing( ResourceHistory::getCreationDate ) )
                .collect( Collectors.toList( ) );
    }

    /**
     * return a list of states according to workflow id
     * 
     * @param nIdWorkflow
     *            the workflow id
     * @return list of states
     */
    private List<State> getStateList( int nIdWorkflow )
    {
        StateFilter stateFilter = new StateFilter( );
        stateFilter.setIdWorkflow( nIdWorkflow );
        return _stateService.getListStateByFilter( stateFilter );
    }

    private List<Workflow> getWorkflowList( )
    {
        WorkflowFilter filter = new WorkflowFilter( );
        filter.setWorkGroup( AdminWorkgroupService.ALL_GROUPS );
        return _workflowService.getListWorkflowsByFilter( filter );
    }

    /**
     * return a list of actions according to workflow id
     * 
     * @param nIdWorkflow
     *            the workflow id
     * @return list of actions
     */
    private List<Action> getActionList( int nIdWorkflow )
    {
        ActionFilter actionFilter = new ActionFilter( );
        actionFilter.setIdWorkflow( nIdWorkflow );
        return _actionService.getListActionByFilter( actionFilter );
    }

    /**
     * return The duration in milliseconds
     * 
     * @param start
     *            The start time.
     * @param end
     *            The end time.
     * @return The duration in days
     */
    private static long duration( java.sql.Timestamp start, java.sql.Timestamp end )
    {
        long milliseconds1 = start.getTime( );
        long milliseconds2 = end.getTime( );
        return milliseconds2 - milliseconds1;
    }
}
