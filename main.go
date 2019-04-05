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

func CreateJob(w http.ResponseWriter, r *http.Request) {
    w.WriteHeader(http.StatusCreated)
    json.NewEncoder(w).Encode("1.0")
}

func main() {

	router := mux.NewRouter()

    router.HandleFunc("/version", GetVersion).Methods("GET")
    router.HandleFunc("/create", CreateJob).Methods("POST")

	log.Fatal(http.ListenAndServe(":8080", router))
}
