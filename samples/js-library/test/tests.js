/*
 * Copyright 2018 Kaidan Gustave
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
const {assert} = require('chai');
const {arrayOf, println} = require('../src/kotlin-like');

describe('println tests', () => {
  it('should print to console', () => {
    println('Hello, World!');
  });
});

describe('arrayOf tests', () => {
  it('should return array', () => {
    assert.isOk(Array.isArray(arrayOf('a', 'b', 'c')));
  });

  it('should return array of proper length', () => {
    assert.equal(3, arrayOf(1, 2, 3).length);
  });

  it('should return array of proper order', () => {
    const array = arrayOf('one', 2, 'thr33');
    assert.equal('one', array[0]);
    assert.equal(2, array[1]);
    assert.equal('thr33', array[2]);
  });
});
