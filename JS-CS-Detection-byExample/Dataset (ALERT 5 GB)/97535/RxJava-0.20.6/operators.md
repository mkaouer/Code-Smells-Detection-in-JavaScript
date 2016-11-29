<style>
   dl { margin-top: 0;
        margin-bottom: 0;
        margin-left: 1.5em; }
   dl#outer>dt { font-weight: bold;
                 font-size: 120%;
                 margin-right: -1em; }
   dt { font-weight: normal;
        margin-right: -1.5em; }
   dl > dt::before { content: "…"; }
   dl#outer > dt::before { content: ""; }
   dd::before { content: ": "; }
   dd.sub::before { content: ""; }
   dt { float: left; clear: left; }
   dd { float: left;
        margin-start: 0;
        -webkit-margin-start: 0;
        margin-left: 1.5em; }
   dd.sub { float: none;
            margin-left: 0; }
   dd.a::after { content: " ⓐⓈ"; }
   dd.b::after { content: " ⓑ"; }
   dd.c::after { content: " ⓒ"; }
   dd.m::after { content: " ⓜ"; }
   dd.s::after { content: " Ⓢ"; }
   hr { clear: both; }
   dl dt, dl dd { background: #fff; }
   dl dl dt, dl dl dd { background: #eef; }
   dl dl dl dt, dl dl dl dd { background: #ffe; }
   dl dl dl dl dt, dl dl dl dl dd { background: #efe; }
   dl dl dl dl dl dt, dl dl dl dl dl dd { background: #fef; }
   dl dl dl dl dl dl dt, dl dl dl dl dl dl dd { background: #fee; }
</style>
<p>
 This tree can help you find the Observable operator you&#8217;re looking for.
</p>
<div>
<dl id="outer">
 <dt>I want to create a new Observable</dt>
  <dd class="sub"><dl>
   <dt>that emits a particular item</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#just"><code>just(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>that was returned from a function called at subscribe-time</dt>
      <dd class="a"><a href="https://github.com/Netflix/RxJava/wiki/Async-Operators#start"><code>start(&#8239;)</code></a></dd>
      <dd class="sub"><dl>
       <dt>anew for each subscriber</dt>
        <dd class="a"><a href="https://github.com/Netflix/RxJava/wiki/Async-Operators#toasync-or-asyncaction-or-asyncfunc"><code>toAsync(&#8239;)</code></a></dd>
      </dl></dd>
     <dt>that was returned from an <code>Action</code> called at subscribe-time</dt>
      <dd class="a"><a href="https://github.com/Netflix/RxJava/wiki/Async-Operators#fromaction"><code>fromAction(&#8239;)</code></a></dd>
     <dt>that was returned from a <code>Callable</code> called at subscribe-time</dt>
      <dd class="a"><a href="https://github.com/Netflix/RxJava/wiki/Async-Operators#fromcallable"><code>fromCallable(&#8239;)</code></a></dd>
     <dt>that was returned from a <code>Runnable</code> called at subscribe-time</dt>
      <dd class="a"><a href="https://github.com/Netflix/RxJava/wiki/Async-Operators#fromrunnable"><code>fromRunnable(&#8239;)</code></a></dd>
     <dt>after a specified delay</dt>
      <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#timer"><code>timer(&#8239;)</code></a></dd>
     </dl></dd>
   <dt>that emits a particular set of 1&ndash;10 items</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#from"><code>from(&#8239;)</code></a></dd>
   <dt>that obtains its sequence from an Array or Iterable</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#from"><code>from(&#8239;)</code></a></dd>
   <dt>by retrieving it from a Future</dt>
    <dd class="a"><a href="https://github.com/Netflix/RxJava/wiki/Async-Operators#deferfuture"><code>deferFuture(&#8239;)</code></a></dd>
   <dt>that obtains its sequence from a Future</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#from"><code>from(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>with a timeout</dt>
     <dd><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#from"><code>from(&#8239;)</code></a></dd>
    </dl></dd>
   <dt>that obtains its sequence from an <code>Action</code> called periodically</dt>
    <dd class="a"><a href="https://github.com/Netflix/RxJava/wiki/Async-Operators#runasync"><code>runAsync(&#8239;)</code></a></dd>
   <dt>that emits a sequence of items repeatedly</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#repeat"><code>repeat(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>as long as a predicate remains true</dt>
      <dd class="c"><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#whileDo"><code>whileDo(&#8239;)</code></a></dd>
      <dd class="sub"><dl>
       <dt>but at least once, no matter what</dt>
       <dd class="c"><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#doWhile"><code>doWhile(&#8239;)</code></a></dd>
      </dl></dd>
    </dl></dd>
   <dt>from scratch, with custom logic</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#create"><code>create(&#8239;)</code></a></dd>
   <dt>for each observer that subscribes</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#defer"><code>defer(&#8239;)</code></a></dd>
   <dt>that emits a sequence of integers</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#range"><code>range(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>at particular intervals of time</dt>
      <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#interval"><code>interval(&#8239;)</code></a></dd>
      <dd class="sub"><dl>
       <dt>after a specified delay</dt>
       <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#timer"><code>timer(&#8239;)</code></a></dd>
      </dl></dd>
    </dl></dd>
   <dt>that completes without emitting items</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#empty"><code>empty(&#8239;)</code></a></dd>
   <dt>that does nothing at all</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#never"><code>never(&#8239;)</code></a></dd>
  </dl></dd>

 <dt>I want to create an Observable by combining other Observables</dt>
  <dd class="sub"><dl>
   <dt>and emitting all of the items from all of the Observables in whatever order they are received</dt>
    <dd class="sub"><dl>
     <dt>where the source Observables are passed to the operator as parameters</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#merge"><code>merge(&hellip;)</code></a></dd>
     <dt>where the source Observables are found in an Array</dt>
      <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#merge"><code>merge(sequences)</code></a></dd>
     <dt>where the source Observables are found in an Iterable or Observable</dt>
      <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#merge"><code>merge(sequences)</code></a></dd>
      <dd class="sub"><dl>
       <dt>but I only want to process a certain number of them at once</dt>
        <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#merge"><code>merge(sequences,maxConcurrent)</code></a></dd>
      </dl></dd>
     <dt>but not forwarding any error notifications until all source Observables have terminated</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#mergedelayerror"><code>mergeDelayError(&#8239;)</code></a></dd>
    </dl></dd>
   <dt>and emitting all of the items from all of the Observables, one Observable at a time</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#concat"><code>concat(&#8239;)</code></a></dd>
   <dt>by combining the items from two or more Observables sequentially to come up with new items to emit</dt>
    <dd class="sub"><dl>
     <dt>whenever <em>each</em> of the Observables has emitted a new item</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#zip"><code>zip(&#8239;)</code></a></dd>
     <dt>whenever <em>any</em> of the Observables has emitted a new item</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#combinelatest"><code>combineLatest(&#8239;)</code></a></dd>
     <dt>whenever an item is emitted by one Observable in a window defined by an item emitted by another</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#join-and-groupjoin"><code>join(&#8239;)</code></a></dd>
      <dd class="sub"><dl>
       <dt>based on an Observable that emits all items that have fallen in such a window</dt>
        <dd><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#join-and-groupjoin"><code>groupJoin(&#8239;)</code></a></dd>
      </dl></dd>
     <dt>by means of <code>Pattern</code> and <code>Plan</code> intermediaries</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#and-then-and-when"><code>and/then/when</code></a></dd>
    </dl></dd>
   <dt>and emitting the items from only the most-recently emitted of those Observables</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#switchonnext"><code>switchOnNext(&#8239;)</code></a></dd>
   <dt>and mirroring only one of those Observables (which one depends on a parameter I am passed)</dt>
    <dd class="c"><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#switchCase"><code>switchCase(&#8239;)</code></a></dd>
   <dt>reducing an Observable that emits many Observables to one that emits as many Observables as I have processes to process them on</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#parallelmerge"><code>parallelMerge(&hellip;)</code></a></dd>
  </dl></dd>

 <dt>I want emit the items from an Observable after transforming them</dt>
  <dd class="sub"><dl>
   <dt>one at a time with a function</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#map"><code>map(&#8239;)</code></a></dd>
   <dt>by casting them to a particular type</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#cast"><code>cast(&#8239;)</code></a></dd>
   <dt>by emitting all of the items emitted by corresponding Observables</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#flatMap"><code>flatMap(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>combined with the original items by means of a function</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#mergemap-and-mergemapiterable"><code>mergeMap(collectionSelector,resultSelector)</code></a></dd>
    </dl></dd>
   <dt>by emitting all of the items in corresponding Iterables</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#mergemap-and-mergemapiterable"><code>mergeMapIterable(collectionSelector)</code></a></dd>
    <dd class="sub"><dl>
     <dt>combined with the original items by means of a function</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#mergemap-and-mergemapiterable"><code>mergeMapIterable(collectionSelector,resultSelector)</code></a></dd>
    </dl></dd>
   <dt>based on all of the items that preceded them</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#scan"><code>scan(&#8239;)</code></a></dd>
   <dt>by combining them sequentially with the items in an Iterable by means of a function</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#zip"><code>zip(iterable,zipFunction)</code></a></dd>
   <dt>by attaching a timestamp to them</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#timestamp"><code>timestamp(&#8239;)</code></a></dd>
   <dt>into an indicator of the amount of time that lapsed before the emission of the item</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#itmeinterval"><code>timeInterval(&#8239;)</code></a></dd>
  </dl></dd>

 <dt>I want to shift the items emitted by an Observable forward in time before reemitting them</dt>
  <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#delay"><code>delay(delay,unit)</code></a></dd>
  <dd class="sub"><dl>
   <dt>with the amount of the shift calculated on a per-item basis</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#delay"><code>delay(itemDelay)</code></a></dd> 
    <dd class="sub"><dl>
     <dt>and the initial subscription to the Observable shifted as well</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#delay"><code>delay(subscriptionDelay,itemDelay)</code></a></dd>
    </dl></dd>
  </dl></dd>

 <dt>I want to transform items <em>and</em> notifications from an Observable into items and reemit them</dt>
  <dd class="sub"><dl>
   <dt>by emitting all of the items emitted by corresponding Observables</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#mergemap-and-mergemapiterable"><code>mergeMap(&#8239;)</code></a></dd>
   <dt>by wrapping them in <code>Notification</code> objects</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#materialize"><code>materialize(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>which I can then unwrap again with</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#dematerialize"><code>dematerialize(&#8239;)</code></a></dd>
    </dl></dd>
  </dl></dd>

 <dt>I want to ignore all items emitted by an Observable and only pass along its completed/error notification</dt>
  <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#ignoreelements"><code>ignoreElements(&#8239;)</code></a></dd>

 <dt>I want to mirror an Observable but prefix items to its sequence</dt>
  <dd class="sub"><dl>
   <dt>obtained from an Array or Iterable</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#startwith"><code>startWith(values)</code></a></dd>
   <dt>obtained from an Observable</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#startwith"><code>startWith(values)</code></a></dd>
   <dt>passed as parameters to the operator</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Combining-Observables#startwith"><code>startWith(&hellip;)</code></a></dd>
   <dt>only if its sequence is empty</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#defaultifempty"><code>defaultIfEmpty(&hellip;)</code></a></dd>
  </dl></dd>

 <dt>I want to collect items from an Observable and reemit them as buffers of items</dt>
  <dd class="sub"><dl>
   <dt>with a maximum number of items per buffer</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#buffer"><code>buffer(count)</code></a></dd>
    <dd class="sub"><dl>
     <dt>and starting every <i>n</i> items</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#buffer"><code>buffer(count,skip)</code></a></dd>
    </dl></dd>
   <dt>each time a second Observable emits an item</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#buffer"><code>buffer(boundary)</code></a></dd>
    <dd class="sub"><dl>
     <dt>with buffers given an initial capacity for efficiency reasons</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#buffer"><code>buffer(boundary,initialCapacity)</code></a></dd>
     <dt>where that second Observable is returned from a function I supply</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#buffer"><code>buffer(bufferClosingSelector)</code></a></dd>
      <dd class="sub"><dl>
       <dt>and operates on the emission of a third Observable that opens the buffer</dt>
        <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#buffer"><code>buffer(bufferOpenings,bufferClosingSelector)</code></a></dd>
      </dl></dd>
     </dl></dd>
    <dt>at periodic intervals</dt>
     <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#buffer"><code>buffer(timespan,unit)</code></a></dd>
     <dd class="sub"><dl>
      <dt>or when a certain maximum number of items fill the buffer</dt>
       <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#buffer"><code>buffer(timespan,unit,count)</code></a></dd>
      <dt>for a certain period of time after the interval begins</dt>
       <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#buffer"><code>buffer(timespan,timeshift,unit)</code></a></dd>
     </dl></dd>
   <dt>containing only the last items emitted</dt>
    <dd class="sub"><dl>
     <dt>that is, the last <i>n</i> items</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#takelastbuffer"><code>takeLastBuffer(count)</code></a></dd>
      <dd class="sub"><dl>
       <dt>emitted during a window of time before the Observable completed</dt>
       <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#takelastbuffer"><code>takeLastBuffer(count,time,unit)</code></a></dd>
      </dl></dd>
     <dt>during a window of time before the Observable completed</dt>
      <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#takelastbuffer"><code>takeLastBuffer(time,unit)</code></a></dd>
    </dl></dd>
  </dl></dd>

 <dt>I want to split one Observable into multiple Observables</dt>
  <dd class="sub"><dl>
   <dt>with a maximum number of items per sub-Observable</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#window"><code>window(count)</code></a></dd>
    <dd class="sub"><dl>
     <dt>and starting every <i>n</i> items</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#window"><code>window(count,skip)</code></a></dd>
    </dl></dd>
   <dt>each time a second Observable emits an item</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#window"><code>window(boundary)</code></a></dd>
    <dd class="sub"><dl>
     <dt>where that second Observable is returned from a function I supply</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#window"><code>window(closingSelector)</code></a></dd>
      <dd class="sub"><dl>
       <dt>and operates on the emission of a third Observable that starts the sub-Observable</dt>
        <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#window"><code>window(windowOpenings,closingSelector)</code></a></dd>
      </dl></dd>
     </dl></dd>
    <dt>at periodic intervals</dt>
     <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#window"><code>window(timespan,unit)</code></a></dd>
     <dd class="sub"><dl>
      <dt>or when a certain maximum number of items have been emitted on the sub-Observable</dt>
       <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#window"><code>window(timespan,unit,count)</code></a></dd>
      <dt>for a certain period of time after the interval begins</dt>
       <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#window"><code>window(timespan,timeshift,unit)</code></a></dd>
     </dl></dd>
   <dt>so that similar items end up on the same Observable</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#groupby-and-groupbyuntil"><code>groupBy(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>but periodically completing some of those Observables even if the source is not complete</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#groupby-and-groupbyuntil"><code>groupByUntil(keySelector,durationSelector)</code></a></dd>
      <dd class="sub"><dl>
       <dt>and transforming the items before emitting them on those Observables</dt>
        <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#groupby-and-groupbyuntil"><code>groupByUntil(keySelector,valueSelector,durationSelector)</code></a></dd>
       <dt>and then collecting similarly grouped Observables back together again</dt>
        <dd><a href="https://github.com/Netflix/RxJava/wiki/Transforming-Observables#pivot"><code>pivot(&#8239;)</code></a></dd>
      </dl></dd>
    </dl></dd>
  </dl></dd>

 <dt>I want to retrieve from an Observable</dt>
  <dd class="sub"><dl>
   <dt>the last item emitted before it completed</dt>
    <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#last-and-lastordefault"><code>last(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>or a default item if none were emitted</dt>
      <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#last-and-lastordefault"><code>lastOrDefault(&#8239;)</code></a></dd>
     <dt>that matches a predicate</dt>
      <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#last-and-lastordefault"><code>last(predicate)</code></a></dd>
      <dd class="sub"><dl>
       <dt>or a default item if none did</dt>
        <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#last-and-lastordefault"><code>lastOrDefault(predicate)</code></a></dd>
      </dl></dd>
    </dl></dd>
   <dt>the sole item it emitted</dt>
    <dd class="sub"><dl>
     <dt>or an exception if it did not emit exactly one</dt>
      <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#single-and-singleordefault"><code>single(&#8239;)</code></a></dd>
      <dd class="sub"><dl>
       <dt>or rather a default item if it did not emit any</dt>
        <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#single-and-singleordefault"><code>singleOrDefault(&#8239;)</code></a></dd>
      </dl></dd>
     <dt>that matches a predicate, or an exception if exactly one did not</dt>
      <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#single-and-singleordefault"><code>single(predicate)</code></a></dd>
      <dd class="sub"><dl>
       <dt>or rather a default item if none did</dt>
        <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#single-and-singleordefault"><code>singleOrDefault(predicate)</code></a></dd>
      </dl></dd>
    </dl></dd>
   <dt>the first item it emitted</dt>
    <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#first-and-firstordefault"><code>first(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>or a default item if none were emitted</dt>
      <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#first-and-firstordefault"><code>firstOrDefault(&#8239;)</code></a></dd>
     <dt>that matches a predicate</dt>
      <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#first-and-firstordefault"><code>first(predicate)</code></a></dd>
      <dd class="sub"><dl>
       <dt>or a default item if none did</dt>
        <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#first-and-firstordefault"><code>firstOrDefault(predicate)</code></a></dd>
      </dl></dd>
    </dl></dd>
  </dl></dd>

 <dt>I want to reemit only certain items from an Observable</dt>
  <dd class="sub"><dl>
   <dt>by filtering out those that do not match some predicate</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#filter"><code>filter(&#8239;)</code></a></dd>
   <dt>by filtering out those that are not of a particular type</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#oftype"><code>ofType(&#8239;)</code></a></dd>
   <dt>that is, only the first item</dt>
    <dd class="sub"><dl>
     <dt>or notify of an error if the source is empty</dt>
     <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#first-and-takefirst"><code>first(&#8239;)</code></a></dd>
     <dt>or a default value if the source is empty</dt>
     <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#firstordefault"><code>firstOrDefault(defaultValue)</code></a></dd>
     <dt>that matches a predicate</dt>
     <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#first-and-takefirst"><code>takeFirst(predicate)</code></a></dd>
     <dd class="sub"><dl>
      <dt>or notify of an error if none do</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#first-and-takefirst"><code>first(predicate)</code></a></dd>
      <dt>or a default value if none do</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#firstordefault"><code>firstOrDefault(defaultValue,predicate)</code></a></dd>
     </dl></dd>
    </dl></dd>
   <dt>that is, only the first item<em>s</em></dt>
    <dd class="sub"><dl>
     <dt>that is, the first <i>n</i> items</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#take"><code>take(num)</code></a></dd>
     <dt>that is, items emitted by the source during an initial period of time</dt>
      <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#take"><code>take(time,unit)</code></a></dd>
    </dl></dd>
   <dt>that is, only the last item</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#last"><code>last(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>that meets some predicate</dt>
     <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#last"><code>last(predicate)</code></a></dd>
     <dd class="sub"><dl>
      <dt>or a default item if none do</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#lastOrDefault"><code>lastOrDefault(predicate)</code></a></dd>
     </dl></dd>
     <dt>or a default item the source emits nothing</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#lastOrDefault"><code>lastOrDefault(&#8239;)</code></a></dd>
    </dl></dd>
   <dt>that is, only item <i>n</i></dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#elementat"><code>elementAt(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>or a default value if there is no item <i>n</i></dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#elementat"><code>elementAtOrDefault(&#8239;)</code></a></dd>
    </dl></dd>
   <dt>that is, only those items after the first items</dt>
    <dd class="sub"><dl>
     <dt>that is, after the first <i>n</i> items</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#skip"><code>skip(num)</code></a></dd>
      <dd class="sub"><dl>
       <dt>or until one of those items matches a predicate</dt>
       <dd><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#skipwhile-and-skipwhilewithindex"><code>skipWhileWithIndex(&#8239;)</code></a></dd>
      </dl></dd>
     <dt>that is, until one of those items matches a predicate</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#skipwhile-and-skipwhilewithindex"><code>skipWhile(&#8239;)</code></a></dd>
     <dt>that is, after an initial period of time</dt>
      <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#skip"><code>skip(time,unit)</code></a></dd>
     <dt>that is, after a second Observable emits an item</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#skipuntil"><code>skipUntil(&#8239;)</code></a></dd>
    </dl></dd>
   <dt>that is, those items except the last items</dt>
    <dd class="sub"><dl>
     <dt>that is, except the last <i>n</i> items</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#skipLast"><code>skipLast(&#8239;)</code></a></dd>
      <dd class="sub"><dl>
       <dt>or until one of those items matches a predicate</dt>
       <dd><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#takewhile-and-takewhilewithindex"><code>takeWhileWithIndex(&#8239;)</code></a></dd>
      </dl></dd>
     <dt>that is, until one of those items matches a predicate</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#takewhile-and-takewhilewithindex"><code>takeWhile(&#8239;)</code></a></dd>
     <dt>that is, except items emitted during a period of time before the source completes</dt>
      <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#skipLast"><code>skipLast(time,unit)</code></a></dd>
     <dt>that is, except items emitted after a second Observable emits an item</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#takeuntil"><code>takeUntil(&#8239;)</code></a></dd>
    </dl></dd>
   <dt>by sampling the Observable periodically</dt>
    <dd class="sub"><dl>
     <dt>based on a timer</dt>
      <dd class="sub"><dl>
       <dt>and emitting the most-recently emitted item in the period</dt>
        <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#sample-or-throttlelast"><code>sample(time,unit)</code></a></dd>
       <dt>and emitting the first-emitted item in the period</dt>
        <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#throttlefirst"><code>throttleFirst(time,unit)</code></a></dd>
      </dl></dd>
     <dt>based on emissions from another Observable</dt>
      <dd class="sub"><dl>
       <dt>and emitting the most-recently emitted item in the period</dt>
        <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#sample-or-throttlelast"><code>sample(sampler)</code></a></dd>
       <dt>and emitting the first-emitted item in the period</dt>
        <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#throttlefirst"><code>throttleFirst(sampler)</code></a></dd>
      </dl></dd>
    </dl></dd>
   <dt>by only emitting items that are not followed by other items within some duration</dt>
    <dd class="sub"><dl>
     <dt>based on a timer</dt>
      <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#throttlewithtimeout-or-debounce"><code>throttleWithTimeout(time,unit)</code></a></dd>
     <dt>based on emissions from another Observable</dt>
      <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#throttlewithtimeout-or-debounce"><code>debounce(debounceSelector)</code></a></dd>
    </dl></dd>
   <dt>by suppressing items that are duplicates of already-emitted items</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#distinct"><code>distinct(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>according to a particular function</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#distinct"><code>distinct(keySelector)</code></a></dd>
     <dt>if they immediately follow the item they are duplicates of</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#distinctuntilchanged"><code>distinctUntilChanged(&#8239;)</code></a></dd>
      <dd class="sub"><dl>
       <dt>according to a particular function</dt>
        <dd><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#distinctuntilchanged"><code>distinctUntilChanged(keySelector)</code></a></dd>
      </dl></dd>
    </dl></dd>
   <dt>by delaying my subscription to it for some time after it begins emitting items</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#delaysubscription"><code>delaySubscription(delay,unit)</code></a></dd>
  </dl></dd>

 <dt>I want to reemit items from an Observable only on condition</dt>
  <dd class="sub"><dl>
   <dt>that it was the first of a collection of Observables to emit an item</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#amb"><code>amb(&#8239;)</code></a></dd>
   <dt>that some predicate is true</dt>
    <dd class="c"><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#ifThen"><code>ifThen(&#8239;)</code></a></dd>
  </dl></dd>

 <dt>I want to evaluate the entire sequence of items emitted by an Observable</dt>
  <dd class="sub"><dl>
   <dt>and emit a single boolean indicating if <em>all</em> of the items pass some test</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#all"><code>all(&#8239;)</code></a></dd>
   <dt>and emit a single boolean indicating if <em>any</em> of the items pass some test</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#contains"><code>contains(&#8239;)</code></a></dd>
   <dt>and emit a single boolean indicating if the Observable emitted <em>any</em> items</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#exists-and-isempty"><code>exists(&#8239;)</code></a></dd>
   <dt>and emit a single boolean indicating if the Observable emitted <em>no</em> items</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#exists-and-isempty"><code>isEmpty(&#8239;)</code></a></dd>
   <dt>and emit a single boolean indicating if the sequence is identical to one emitted by a second Observable</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Conditional-and-Boolean-Operators#sequenceequal"><code>sequenceEqual(&#8239;)</code></a></dd>
   <dt>and emit the average of all of their values</dt>
    <dd class="m"><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#averageinteger-averagelong-averagefloat-and-averagedouble"><code>average</code><i>Type</i><code>(&#8239;)</code></a></dd>
   <dt>and emit the sum of all of their values</dt>
    <dd class="m"><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#suminteger-sumlong-sumfloat-and-sumdouble"><code>sum</code><i>Type</i><code>(&#8239;)</code></a></dd>
   <dt>and emit a number indicating how many items were in the sequence</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#count-and-longcount">[<code>long</code>]<code>count(&#8239;)</code></a></dd>
   <dt>and emit the item with the maximum value</dt>
    <dd class="m"><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#max"><code>max(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>according to some value-calculating function</dt>
      <dd class="m"><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#maxby"><code>maxBy(&#8239;)</code></a></dd>
    </dl></dd>
   <dt>and emit the item with the minimum value</dt>
    <dd class="m"><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#min"><code>min(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>according to some value-calculating function</dt>
      <dd class="m"><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#minby"><code>minBy(&#8239;)</code></a></dd>
    </dl></dd>
   <dt>by applying an aggregation function to each item in turn and emitting the result</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#reduce"><code>reduce(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>in the form of a single mutable data structure</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#collect"><code>collect(&#8239;)</code></a></dd>
    </dl></dd>
   <dt>by applying a function to each item in the sequence, blocking until complete</dt>
    <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#foreach"><code>forEach(&#8239;)</code></a></dd>
  </dl></dd>

 <dt>I want to convert the entire sequence of items emitted by an Observable</dt>
  <dd class="sub"><dl>
   <dt>into a Future</dt>
    <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#transformations-tofuture-toiterable-and-toiteratorgetiterator"><code>toFuture(&#8239;)</code></a></dd>
   <dt>into an Iterable</dt>
    <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#transformations-tofuture-toiterable-and-toiteratorgetiterator"><code>toIterable(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>that returns the most recently item emitted by the Observable</dt>
      <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#mostrecent"><code>mostRecent(&#8239;)</code></a></dd>
      <dd class="sub"><dl>
       <dt>only if it has not previously returned that item</dt>
        <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#latest"><code>latest(&#8239;)</code></a></dd>
      </dl></dd>
     <dt>that returns the next item when it is emitted by the Observable</dt>
      <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#latest"><code>latest(&#8239;)</code></a></dd>
    </dl></dd>
   <dt>into an Iterator</dt>
    <dd class="b"><a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators#transformations-tofuture-toiterable-and-toiteratorgetiterator"><code>getIterator(&#8239;)</code> or <code>toIterator(&#8239;)</code></a></dd>
   <dt>into a List</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#tolist"><code>toList(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>sorted by some criterion</dt>
     <dd><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#tosortedlist"><code>toSortedList(&#8239;)</code></a></dd>
    </dl></dd>
   <dt>into a Map</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#tomap"><code>toMap(&#8239;)</code></a></dd>
    <dd class="sub"><dl>
     <dt>that is also an ArrayList</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Mathematical-and-Aggregate-Operators#tomultimap"><code>toMultiMap(&#8239;)</code></a></dd>
    </dl></dd>
  </dl></dd>

 <dt>I want an Observable to emit exactly one item</dt>
  <dd class="sub"><dl>
   <dt>so I want it to notify of an error otherwise</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#single"><code>single(&#8239;)</code></a></dd>
   <dt>so I want it to notify of an error if it emits more than one, or a default item if it emits none</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#single"><code>singleOrDefault(&#8239;)</code></a></dd>
   <dt>that matches a predicate</dt>
    <dd class="sub"><dl>
     <dt>so I want it to notify of an error otherwise</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#single"><code>single(predicate)</code></a></dd>
     <dt>so I want it to notify of an error if it emits more than one, or a default item if it emits none</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#single"><code>singleorDefault(predicate)</code></a></dd>
    </dl></dd>
  </dl></dd>

 <dt>I want an operator to operate on a particular Scheduler</dt>
  <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#subscribeon"><code>subscribeOn(&#8239;)</code></a></dd>
  <dd class="sub"><dl>
   <dt>doing its processing in parallel on multiple threads without making the resulting Observable poorly-behaved</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#parallel"><code>parallel(&#8239;)</code></a></dd>
   <dt>when it notifies Observers</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#observeon"><code>observeOn(&#8239;)</code></a></dd>
  </dl></dd>

 <dt>I want an Observable to invoke a particular action</dt>
  <dd class="sub"><dl>
   <dt>whenever it emits an item</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#dooneach"><code>doOnEach(action)</code></a></dd>
   <dt>when it issues a completed notification</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#dooncompleted"><code>doOnCompleted(action)</code></a></dd>
   <dt>when it issues an error notification</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#doonerror"><code>doOnError(action)</code></a></dd>
   <dt>when it issues a completed or error notification</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#doonterminate"><code>doOnTerminate(action)</code></a></dd>
   <dt>after it has issued a completed or error notification</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#finallydo"><code>finallyDo(action)</code></a></dd>
   <dt>whenever it emits an item or issues a completed/error notification</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#dooneach"><code>doOnEach(observer)</code></a></dd>
  </dl></dd>

 <dt>I want an Observable that will notify observers of an error</dt>
  <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Creating-Observables#error"><code>error(&#8239;)</code></a></dd>
  <dd class="sub"><dl>
   <dt>if a specified period of time elapses without it emitting an item</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#timeout"><code>timeout(time,unit)</code></a></dd>
  </dl></dd>

 <dt>I want an Observable to recover gracefully</dt>
  <dd class="sub"><dl>
   <dt>from a timeout by switching to a backup Observable</dt>
    <dd class="s"><a href="https://github.com/Netflix/RxJava/wiki/Filtering-Observables#timeout"><code>timeout(time,unit,fallback)</code></a></dd>
   <dt>from an upstream error notification</dt>
    <dd class="sub"><dl>
     <dt>by switching to a particular backup Observable</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Error-Handling-Operators#onerrorresumenext"><code>onErrorResumeNext(sequence)</code></a></dd>
      <dd class="sub"><dl>
       <dt>but only if the error is an <code>Exception</code></dt>
        <dd><a href="https://github.com/Netflix/RxJava/wiki/Error-Handling-Operators#onexceptionresumenext"><code>onExceptionResumeNext(&#8239;)</code></a></dd>
      </dl></dd>
     <dt>by switching to a backup Observable returned from a function that is passed the error</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Error-Handling-Operators#onerrorresumenext"><code>onErrorResumeNext(throwable,function)</code></a></dd>
      <dd class="sub"><dl>
       <dt>and by then continuing to observe the source Observable in spite of the error termination</dt>
        <dd><a href="https://github.com/Netflix/RxJava/wiki/Error-Handling-Operators#onerrorflatmap"><code>onErrorFlatMap(&#8239;)</code></a></dd>
      </dl></dd>
     <dt>by emitting a particular item and completing normally</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Error-Handling-Operators#onerrorreturn"><code>onErrorReturn(&#8239;)</code></a></dd>
     <dt>by attempting to resubscribe to the upstream Observable</dt>
      <dd><a href="https://github.com/Netflix/RxJava/wiki/Error-Handling-Operators#retry"><code>retry(&#8239;)</code></a></dd>
      <dd class="sub"><dl>
       <dt>a certain number of times</dt>
        <dd><a href="https://github.com/Netflix/RxJava/wiki/Error-Handling-Operators#retry"><code>retry(count)</code></a></dd>
       <dt>so long as a predicate remains true</dt>
        <dd><a href="https://github.com/Netflix/RxJava/wiki/Error-Handling-Operators#retry"><code>retry(predicate)</code></a></dd>
      </dl></dd>
    </dl></dd>
   <dt>from being potentially unserialized or otherwise poorly-behaved</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#serialize"><code>serialize(&#8239;)</code></a></dd>
  </dl></dd>

 <dt>I want to create a resource that has the same lifespan as the Observable</dt>
  <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#using"><code>using(&#8239;)</code></a></dd>

 <dt>I want to subscribe to an Observable and receive a <code>Future</code> that blocks until the Observable completes</dt>
  <dd class="a"><a href="https://github.com/Netflix/RxJava/wiki/Async-Operators#foreachfuture"><code>forEachFuture(&#8239;)</code></a></dd>

 <dt>I want an Observable that does not start emitting items to subscribers until asked</dt>
  <dd><a href="https://github.com/Netflix/RxJava/wiki/Connectable-Observable-Operators#observablepublish-and-observablemulticast"><code>publish(&#8239;)</code> or <code>multicast(&#8239;)</code></a></dd>
  <dd class="sub"><dl>
   <dt>and then only emits the last item in its sequence</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Connectable-Observable-Operators#observablepublishlast"><code>publishLast(&#8239;)</code></a></dd>
   <dt>and then emits the complete sequence, even to those who subscribe after the sequence has begun</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Connectable-Observable-Operators#observablereplay"><code>replay(&#8239;)</code></a></dd>
   <dt>but I want it to go away once all of its subscribers unsubscribe</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Connectable-Observable-Operators#connectableobservablerefcount"><code>refCount(&#8239;)</code> or <code>share(&#8239;)</code></a></dd>
   <dt>and then I want to ask it to start</dt>
    <dd><a href="https://github.com/Netflix/RxJava/wiki/Connectable-Observable-Operators#connectableobservableconnect"><code>connect(&#8239;)</code></a></dd>
  </dl></dd>

 <dt>I want an Observable to retransmit items to observers who subscribe late</dt>
  <dd><a href="https://github.com/Netflix/RxJava/wiki/Observable-Utility-Operators#cache"><code>cache(&#8239;)</code></a></dd>

</dl>
</div>
<hr />
<p>
 ⓐ: this operator is part of the optional <code>async-util</code> package<br />
 ⓑ: this operator is part of the <code>BlockingObservable</code> subclass<br />
 ⓒ: this operator is part of the optional <code>computation-expressions</code> package<br />
 ⓜ: this operator is part of the optional <code>math</code> package<br />
 Ⓢ: a variant of this operator allows you to choose a particular Scheduler<br />
</p><p>
 I have omitted parameter names from some methods where they are not necessary to distinguish variants of the method. This page was inspired by the RxJS tables (<a href="https://github.com/trxcllnt/RxJS/blob/master/doc/static-operators.md">static</a> and <a href="https://github.com/trxcllnt/RxJS/blob/master/doc/instance-operators.md">instance</a>) created by Paul Taylor.
</p>
