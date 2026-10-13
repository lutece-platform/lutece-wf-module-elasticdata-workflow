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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.elasticdata.business.IndexerAction;
import fr.paris.lutece.plugins.elasticdata.business.IndexerActionHome;
import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.service.event.EventAction;
import fr.paris.lutece.portal.service.event.Type.TypeQualifier;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

/**
 * Tests of the workflow resource event listener
 */
public class WorkflowEventListenerTest extends LuteceTestCase
{
    private static final String DATA_SOURCE_ID = "WorkflowDataSource";
    private static final String ID_WATCHED = "876541";
    private static final String ID_UNWATCHED = "876542";
    private static final long TIMEOUT = 5000L;

    @Inject
    private Event<ResourceEvent> _resourceEvent;

    /**
     * Removes the queued tasks
     */
    @AfterEach
    public void removeTasks( )
    {
        for ( String strId : new String [ ] {
                ID_WATCHED, ID_UNWATCHED
        } )
        {
            IndexerAction indexerAction = IndexerActionHome.findByIdResource( strId, DATA_SOURCE_ID );
            if ( indexerAction != null )
            {
                IndexerActionHome.remove( indexerAction.getId( ) );
            }
        }
    }

    /**
     * An update of a watched resource type queues a creation task, another type queues nothing
     *
     * @throws InterruptedException
     *             if the wait is interrupted
     */
    @Test
    public void testUpdatedResourceQueuesTask( ) throws InterruptedException
    {
        fireUpdate( ID_UNWATCHED, "FORMS_FORM_RESPONSE" );
        fireUpdate( ID_WATCHED, "USERGRU" );

        IndexerAction indexerAction = waitForTask( ID_WATCHED );

        assertNotNull( indexerAction );
        assertEquals( IndexerAction.TASK_CREATE, indexerAction.getIdTask( ) );
        assertNull( IndexerActionHome.findByIdResource( ID_UNWATCHED, DATA_SOURCE_ID ) );
    }

    /**
     * Fires a resource update event
     *
     * @param strIdResource
     *            the resource id
     * @param strResourceType
     *            the resource type
     */
    private void fireUpdate( String strIdResource, String strResourceType )
    {
        ResourceEvent event = new ResourceEvent( );
        event.setIdResource( strIdResource );
        event.setTypeResource( strResourceType );
        _resourceEvent.select( new TypeQualifier( EventAction.UPDATE ) ).fire( event );
    }

    /**
     * Waits for the task the listener queues on its executor
     *
     * @param strIdResource
     *            the resource id
     * @return the queued task, or null after the timeout
     * @throws InterruptedException
     *             if the wait is interrupted
     */
    private IndexerAction waitForTask( String strIdResource ) throws InterruptedException
    {
        long lDeadline = System.currentTimeMillis( ) + TIMEOUT;
        IndexerAction indexerAction = IndexerActionHome.findByIdResource( strIdResource, DATA_SOURCE_ID );
        while ( indexerAction == null && System.currentTimeMillis( ) < lDeadline )
        {
            Thread.sleep( 100 );
            indexerAction = IndexerActionHome.findByIdResource( strIdResource, DATA_SOURCE_ID );
        }
        return indexerAction;
    }
}
