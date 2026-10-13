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
package fr.paris.lutece.plugins.elasticdata.modules.workflow.service.listener;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.elasticdata.business.IndexerAction;
import fr.paris.lutece.plugins.elasticdata.modules.workflow.business.WorkflowDataSource;
import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.service.event.EventAction;
import fr.paris.lutece.portal.service.event.Type;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

/**
 * Queues the indexing of the workflow resources on resource events
 */
@ApplicationScoped
public class WorkflowEventListener
{
    @Inject
    private WorkflowDataSource _workflowDataSource;
    @Inject
    private ManagedExecutorService _managedExecutor;
    @Inject
    @ConfigProperty( name = "elasticdata-workflow.workflow.ressource.type.list" )
    private Optional<String> _workflowResourceTypes;
    private List<String> _listWorkflowResourceType;

    /**
     * Reads the watched resource types, comma separated
     */
    @PostConstruct
    void initResourceTypes( )
    {
        _listWorkflowResourceType = Arrays.stream( StringUtils.split( _workflowResourceTypes.orElse( StringUtils.EMPTY ), ',' ) ).map( String::trim )
                .collect( Collectors.toList( ) );
    }

    /**
     * Handles the event for an added resource
     *
     * @param event
     *            the event for the added resource
     */
    public void addedResource( @Observes @Type( EventAction.CREATE ) ResourceEvent event )
    {
        _managedExecutor.execute( ( ) -> indexResource( event, IndexerAction.TASK_CREATE ) );
    }

    /**
     * Handles the event for a deleted resource
     *
     * @param event
     *            the event for the deleted resource
     */
    public void deletedResource( @Observes @Type( EventAction.REMOVE ) ResourceEvent event )
    {
        _managedExecutor.execute( ( ) -> indexResource( event, IndexerAction.TASK_DELETE ) );
    }

    /**
     * Handles the event for an updated resource
     *
     * @param event
     *            the event for the updated resource
     */
    public void updatedResource( @Observes @Type( EventAction.UPDATE ) ResourceEvent event )
    {
        _managedExecutor.execute( ( ) -> indexResource( event, IndexerAction.TASK_CREATE ) );
    }

    /**
     * Queues the indexing task of the event resource when its type is watched
     *
     * @param event
     *            the resource event
     * @param nIdTask
     *            the indexing task
     */
    private void indexResource( ResourceEvent event, int nIdTask )
    {
        if ( !checkResourceType( event ) )
        {
            return;
        }
        try
        {
            int nIdResource = Integer.parseInt( event.getIdResource( ) );
            _workflowDataSource.indexDocument( nIdResource, nIdTask );
        }
        catch( NumberFormatException e )
        {
            AppLogService.error( "Unable to parse given event id resource to integer {}", event.getIdResource( ), e );
        }
        catch( RuntimeException e )
        {
            AppLogService.error( "Unable to queue the indexing of the resource {}", event.getIdResource( ), e );
        }
    }

    /**
     * Checks whether the event resource type is watched
     *
     * @param event
     *            the resource event
     * @return true if the resource type is watched
     */
    private boolean checkResourceType( ResourceEvent event )
    {
        return _listWorkflowResourceType.contains( event.getTypeResource( ) );
    }
}
