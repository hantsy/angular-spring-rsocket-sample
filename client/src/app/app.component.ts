import { Component, OnDestroy, OnInit } from '@angular/core';
import { IdentitySerializer, JsonSerializer, RSocket, RSocketClient } from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';
import { Subject } from 'rxjs';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {

  title = 'client';
  message = '';
  messages: any[];
  client: RSocketClient;
  sub = new Subject();

  ngOnInit(): void {
    this.messages = [];

    // Create an instance of a client
    this.client = new RSocketClient({
      serializers: {
        data: JsonSerializer,
        metadata: IdentitySerializer
      },
      setup: {
        // ms btw sending keepalive to server
        keepAlive: 60000,
        // ms timeout if no keepalive response
        lifetime: 180000,
        // format of `data`
        dataMimeType: 'application/json',
        // format of `metadata`
        metadataMimeType: 'message/x.rsocket.routing.v0',
      },
      transport: new RSocketWebSocketClient({
        url: 'ws://localhost:8080/rsocket'
      }),
    });

    // Open the connection
    this.client.connect().subscribe({
      onComplete: (socket: RSocket) => {

        // socket provides the rsocket interactions fire/forget, request/response,
        // request/stream, etc as well as methods to close the socket.
        socket
          .requestStream({
            data: null,
            metadata: String.fromCharCode('messages'.length) + 'messages'
          })
          .subscribe({
            onComplete: () => console.log('complete'),
            onError: error => {
              console.log("Connection has been closed due to:: " + error);
            },
            onNext: payload => {
              console.log(payload);
              this.addMessage(payload.data);
            },
            onSubscribe: subscription => {
              subscription.request(1000000);
            },
          });

        this.sub.subscribe({
          next: (data) => {
            socket.fireAndForget({
              data: data,
              metadata: String.fromCharCode('send'.length) + 'send',
            });
          }
        })
      },
      onError: error => {
        console.log("Connection has been refused due to:: " + error);
      },
      onSubscribe: cancel => {
        /* call cancel() to abort */
      }
    });
  }

  addMessage(newMessage: any) {
    console.log("add message:" + JSON.stringify(newMessage))
    this.messages = [...this.messages, newMessage];
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
    if (this.client) {
      this.client.close();
    }
  }

  sendMessage() {
    console.log("sending message:" + this.message);
    this.sub.next(this.message);
    this.message = '';
  }
}
