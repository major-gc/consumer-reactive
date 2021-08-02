package com.example.consumer.stock.model

data class StockListingInformation (
        private val resultCode: Int = 0,//결과코드
        private val resultMsg: String? = null,//결과메시지
        private val numOfRows: Int = 0,//한 페이지 결과 수
        private val pageNo: Int = 0,//페이지 번호
        private val totalCount: Int = 0,//전체 결과 수
        private val apliDt: String? = null,//상장일자
        private val dlistDt: String? = null,//상장폐지일자
        private val isin: String? = null,//종목번호
        private val korSecnNm: String? = null,//국문 종목명
        private val listTpcd: Int? = null,//상장구분코드
        private val xpitDt: String? = null,//만료일자
)