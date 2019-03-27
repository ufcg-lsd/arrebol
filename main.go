package main

import (
    "encoding/json"
	"github.com/gorilla/mux"
	"log"
	"net/http"
)

func GetVersion(w http.ResponseWriter, r *http.Request) {
    json.NewEncoder(w).Encode("1.0")
}

func main() {
	router := mux.NewRouter()
    router.HandleFunc("/version", GetVersion).Methods("GET")
	log.Fatal(http.ListenAndServe(":8080", router))
}
