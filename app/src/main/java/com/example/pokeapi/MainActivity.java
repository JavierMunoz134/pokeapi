package com.example.pokeapi;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private int limit = 20;
    private int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuración de Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Llamada para obtener la lista de Pokémon
        getPokemonList(limit, offset);
    }

    private void getPokemonList(final int limit, final int offset) {
        PokeApiService service = retrofit.create(PokeApiService.class);
        Call<PokemonList> pokeListCall = service.getPokemonList(limit, offset);

        pokeListCall.enqueue(new Callback<PokemonList>() {
            @Override
            public void onResponse(Call<PokemonList> call, Response<PokemonList> response) {
                if (response.isSuccessful()) {
                    PokemonList pokemonList = response.body();
                    if (pokemonList != null && pokemonList.getResults() != null) {
                        // Iterar sobre la lista de Pokémon y mostrar los nombres
                        for (Pokemon pokemon : pokemonList.getResults()) {
                            Log.d("POKEMON NAME", pokemon.getName());
                        }

                        // Si hay más Pokémon, hacer otra llamada recursiva
                        if (pokemonList.getResults().size() == limit) {
                            // Actualizar el offset y realizar la llamada recursiva
                            getPokemonList(limit, offset + limit);
                        }
                    }
                } else {
                    // Manejar el código de error si es necesario
                    Log.e("API Error", "Failed to fetch Pokémon list. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PokemonList> call, Throwable t) {
                // Manejar el error de la llamada
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

