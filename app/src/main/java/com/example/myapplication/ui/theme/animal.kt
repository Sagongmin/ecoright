package com.example.myapplication.ui.theme

class animal {
    var name: String = "" // 동물 이름
    var kind: String = "" // 동물 종류

    constructor() // 생성자 메서드

    constructor(name: String, kind: String) { // 값 추가할 때 쓰는 생성자
        this.name = name
        this.kind = kind
    }
}